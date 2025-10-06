package my.hw6;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomExecutorService extends AbstractExecutorService {
    private final int corePoolSize;
    private final boolean useVirtualThreads;
    private final BlockingQueue<Runnable> taskQueue;
    private final List<Thread> workers;
    private final AtomicBoolean isShutdown;
    private final AtomicBoolean isTerminated;
    private final AtomicInteger activeTasks;

    public CustomExecutorService(int corePoolSize, boolean useVirtualThreads) {
        if (corePoolSize <= 0) {
            throw new IllegalArgumentException("corePoolSize must be > 0");
        }
        this.corePoolSize = corePoolSize;
        this.useVirtualThreads = useVirtualThreads;
        this.taskQueue = useVirtualThreads ? null : new LinkedBlockingQueue<>();
        this.workers = useVirtualThreads ? List.of() : new ArrayList<>(corePoolSize);
        this.isShutdown = new AtomicBoolean(false);
        this.isTerminated = new AtomicBoolean(false);
        this.activeTasks = new AtomicInteger(0);

        if (!useVirtualThreads) {
            for (int i = 0; i < corePoolSize; i++) {
                Thread worker = new Thread(this::workerLoop, "custom-exec-worker-" + i);
                worker.setDaemon(false);
                worker.start();
                workers.add(worker);
            }
        }
    }

    @Override
    public void execute(Runnable command) {
        Objects.requireNonNull(command, "command");
        if (isShutdown.get()) {
            throw new RejectedExecutionException("Executor is shutdown");
        }
        Runnable wrapped = wrapForAccounting(command);
        if (useVirtualThreads) {
            Thread.startVirtualThread(wrapped);
        } else {
            if (!taskQueue.offer(wrapped)) {
                throw new RejectedExecutionException("Task queue rejected the task");
            }
        }
    }

    private Runnable wrapForAccounting(Runnable task) {
        activeTasks.incrementAndGet();
        return () -> {
            try {
                task.run();
            } finally {
                int remaining = activeTasks.decrementAndGet();
                tryTerminateIfIdle(remaining);
            }
        };
    }

    private <T> Future<T> submitCallable(Callable<T> task) {
        Objects.requireNonNull(task, "task");
        FutureTask<T> ft = new FutureTask<>(task);
        execute(ft);
        return ft;
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return submitCallable(task);
    }

    @Override
    public Future<?> submit(Runnable task) {
        return submitCallable(() -> {
            task.run();
            return null;
        });
    }

    @Override
    public void shutdown() {
        if (!isShutdown.compareAndSet(false, true)) {
            return;
        }
        if (!useVirtualThreads) {
            for (int i = 0; i < workers.size(); i++) {
                taskQueue.offer(POISON);
            }
        }
        tryTerminateIfIdle(activeTasks.get());
    }

    @Override
    public List<Runnable> shutdownNow() {
        shutdown();
        List<Runnable> pending = new ArrayList<>();
        if (!useVirtualThreads && taskQueue != null) {
            taskQueue.drainTo(pending);
        }
        if (!useVirtualThreads) {
            for (Thread w : workers) {
                w.interrupt();
            }
        }
        return pending;
    }

    @Override
    public boolean isShutdown() {
        return isShutdown.get();
    }

    @Override
    public boolean isTerminated() {
        return isTerminated.get();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        final long deadline = System.nanoTime() + nanos;
        while (System.nanoTime() < deadline) {
            if (isTerminated()) {
                return true;
            }
            Thread.sleep(5);
            tryTerminateIfIdle(activeTasks.get());
        }
        return isTerminated();
    }

    private void tryTerminateIfIdle(int remainingActive) {
        if (!isShutdown.get()) return;
        if (!useVirtualThreads) {
            boolean workersStopped = true;
            for (Thread w : workers) {
                if (w.isAlive()) {
                    workersStopped = false;
                    break;
                }
            }
            if (workersStopped && remainingActive == 0 && taskQueue.isEmpty()) {
                isTerminated.compareAndSet(false, true);
            }
        } else {
            if (remainingActive == 0) {
                isTerminated.compareAndSet(false, true);
            }
        }
    }

    private void workerLoop() {
        try {
            for (;;) {
                Runnable task = taskQueue.take();
                if (task == POISON) {
                    break;
                }
                task.run();
            }
        } catch (InterruptedException ie) {
        } finally {
            tryTerminateIfIdle(activeTasks.get());
        }
    }

    private static final Runnable POISON = () -> {};
}


