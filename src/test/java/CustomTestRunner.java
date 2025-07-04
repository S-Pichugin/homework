import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class CustomTestRunner {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_YELLOW = "\u001B[33m";

    private static final AtomicInteger totalTests = new AtomicInteger(0);
    private static final AtomicInteger passedTests = new AtomicInteger(0);
    private static final AtomicInteger failedTests = new AtomicInteger(0);
    private static long totalExecutionTime = 0;


    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {

        var metods = CustomListTest.class.getMethods();
        var testMetodsList = Arrays.stream(metods).filter(method -> method.getName().startsWith("test")).toList();


        for (var method : testMetodsList) {
            if (method.isAnnotationPresent(ParameterizedTest.class) && method.isAnnotationPresent(MethodSource.class)) {
                runParameterizedTest(method);
            }
        }

        printSummary();
    }

    private static void runParameterizedTest(Method testMethod) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {

        Method providerMethod = CustomListTest.class.getDeclaredMethod("listProvider");
        Object testInstance = CustomListTest.class.getDeclaredConstructor().newInstance();
        Stream<?> argumentsStream = (Stream<?>) providerMethod.invoke(testInstance);
        List<?> argumentsList = argumentsStream.toList();
        for (Object arguments : argumentsList) {
            totalTests.incrementAndGet();
            Object[] params = new Object[]{arguments};

            try {
                System.out.println("Test: " + testMethod.getName());
                System.out.printf("Running test for %s%n", arguments.getClass());
                long startTime = System.nanoTime();
                testMethod.invoke(testInstance, params);
                long endTime = System.nanoTime();
                long duration = endTime - startTime;
                totalExecutionTime += duration;
                System.out.println(ANSI_GREEN + "Test PASSED" + "(" + duration + " ns)" + ANSI_RESET);
                passedTests.incrementAndGet();
            } catch (InvocationTargetException e) {
                System.out.println(ANSI_RED + "Test FAILED: " + e.getCause().getMessage() + ANSI_RESET);
                failedTests.incrementAndGet();
            }
            System.out.println("--------------------");
        }
    }

    private static void printSummary() {
        double successRate = (double) passedTests.get() / totalTests.get() * 100;

        System.out.println(ANSI_CYAN + "\nСводка:" + ANSI_RESET);
        System.out.println(ANSI_YELLOW + "----------------------------" + ANSI_RESET);
        System.out.printf("Всего тестов: %d%n", totalTests.get());
        System.out.printf("Пройдено: %d%n", passedTests.get());
        System.out.printf("Не пройдено: %d%n", failedTests.get());
        System.out.printf("Общее время выполнения: %.2f ms%n", totalExecutionTime / 1_000_000.0);
        System.out.printf("Успех: %.1f%%%n", successRate);
        System.out.println(ANSI_YELLOW + "----------------------------" + ANSI_RESET);
    }
}
