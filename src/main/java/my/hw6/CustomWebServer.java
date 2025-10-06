package my.hw6;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class CustomWebServer {
    private final int port;
    private final CustomExecutorService executor;
    private ServerSocket serverSocket;
    private final AtomicBoolean running = new AtomicBoolean(false);

    private final Instant startTime = Instant.now();
    private final AtomicLong requestsServed = new AtomicLong(0);

    public CustomWebServer(int port, int threadPoolSize, boolean useVirtualThreads) {
        this.port = port;
        this.executor = new CustomExecutorService(threadPoolSize, useVirtualThreads);
    }

    public void start() throws IOException {
        if (!running.compareAndSet(false, true)) return;
        serverSocket = new ServerSocket(port);
        Thread acceptor = new Thread(() -> {
            while (running.get()) {
                try {
                    Socket socket = serverSocket.accept();
                    executor.execute(() -> handleClient(socket));
                } catch (IOException e) {
                    if (running.get()) {
                        e.printStackTrace();
                    }
                }
            }
        }, "custom-web-acceptor-" + port);
        acceptor.setDaemon(true);
        acceptor.start();
    }

    public void stop() {
        if (!running.compareAndSet(true, false)) return;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException ignored) {}
        executor.shutdown();
        try {
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void handleClient(Socket clientSocket) {
        try (Socket socket = clientSocket;
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.US_ASCII));
             OutputStream rawOut = new BufferedOutputStream(socket.getOutputStream())) {

            String requestLine = reader.readLine();
            if (requestLine == null || requestLine.isEmpty()) {
                return;
            }
            String[] parts = requestLine.split(" ", 3);
            if (parts.length < 3) {
                writeResponse(rawOut, 400, "Bad Request", "text/plain", "Bad Request".getBytes(StandardCharsets.UTF_8));
                return;
            }
            String method = parts[0];
            String path = parts[1];
            String version = parts[2];

            Map<String, String> headers = new HashMap<>();
            String line;
            int contentLength = 0;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                int idx = line.indexOf(":");
                if (idx > 0) {
                    String hName = line.substring(0, idx).trim().toLowerCase(Locale.ROOT);
                    String hVal = line.substring(idx + 1).trim();
                    headers.put(hName, hVal);
                    if (hName.equals("content-length")) {
                        try { contentLength = Integer.parseInt(hVal); } catch (NumberFormatException ignored) {}
                    }
                }
            }

            byte[] body = null;
            if ("POST".equalsIgnoreCase(method) && contentLength > 0) {
                body = readFixedBytes(socket.getInputStream(), contentLength);
            }

            requestsServed.incrementAndGet();


            if ("GET".equalsIgnoreCase(method)) {
                if ("/".equals(path)) {
                    serveStaticFile(rawOut, "/static/index.html");
                } else if (path.startsWith("/static/")) {
                    serveStaticFile(rawOut, path);
                } else if ("/api/time".equals(path)) {
                    String json = "{\"now\":\"" + Instant.now() + "\"}";
                    writeResponse(rawOut, 200, "OK", "application/json", json.getBytes(StandardCharsets.UTF_8));
                } else if ("/api/stats".equals(path)) {
                    long uptimeMs = Duration.between(startTime, Instant.now()).toMillis();
                    String json = "{\"requests\":" + requestsServed.get() + ",\"uptimeMs\":" + uptimeMs + "}";
                    writeResponse(rawOut, 200, "OK", "application/json", json.getBytes(StandardCharsets.UTF_8));
                } else {
                    writeResponse(rawOut, 404, "Not Found", "text/plain", "Not Found".getBytes(StandardCharsets.UTF_8));
                }
            } else if ("POST".equalsIgnoreCase(method) && "/api/echo".equals(path)) {
                byte[] resp = body == null ? new byte[0] : body;
                writeResponse(rawOut, 200, "OK", "text/plain", resp);
            } else {
                writeResponse(rawOut, 405, "Method Not Allowed", "text/plain", "Method Not Allowed".getBytes(StandardCharsets.UTF_8));
            }
        } catch (IOException ignored) {
        }
    }

    private void serveStaticFile(OutputStream out, String urlPath) throws IOException {
        String normalized = urlPath.startsWith("/") ? urlPath : "/" + urlPath;
        if (!normalized.startsWith("/static/")) {
            normalized = "/static/index.html";
        }

        byte[] data = readResource(normalized);
        if (data == null) {
            writeResponse(out, 404, "Not Found", "text/plain", "Not Found".getBytes(StandardCharsets.UTF_8));
            return;
        }
        String mime = guessContentType(normalized);
        writeResponse(out, 200, "OK", mime, data);
    }

    private String guessContentType(String path) {
        String mime = URLConnection.guessContentTypeFromName(path);
        if (mime == null) mime = "application/octet-stream";
        return mime;
    }

    private byte[] readResource(String resourcePath) throws IOException {
        try (InputStream in = getClass().getResourceAsStream(resourcePath)) {
            if (in != null) {
                return readAllBytes(in);
            }
        }
        File file = new File("." + resourcePath).getCanonicalFile();
        if (file.exists() && file.isFile()) {
            try (InputStream in = new BufferedInputStream(new FileInputStream(file))) {
                return readAllBytes(in);
            }
        }
        return null;
    }

    private static byte[] readAllBytes(InputStream in) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        byte[] buf = new byte[8192];
        int r;
        while ((r = in.read(buf)) != -1) {
            bout.write(buf, 0, r);
        }
        return bout.toByteArray();
    }

    private static byte[] readFixedBytes(InputStream in, int length) throws IOException {
        byte[] buf = new byte[length];
        int read = 0;
        while (read < length) {
            int r = in.read(buf, read, length - read);
            if (r == -1) break;
            read += r;
        }
        if (read < length) {
            byte[] exact = new byte[read];
            System.arraycopy(buf, 0, exact, 0, read);
            return exact;
        }
        return buf;
    }

    private static void writeResponse(OutputStream out, int code, String reason, String contentType, byte[] body) throws IOException {
        String headers = "HTTP/1.1 " + code + " " + reason + "\r\n" +
                "Content-Type: " + contentType + "\r\n" +
                "Content-Length: " + body.length + "\r\n" +
                "Connection: close\r\n" +
                "\r\n";
        out.write(headers.getBytes(StandardCharsets.US_ASCII));
        out.write(body);
        out.flush();
    }
}


