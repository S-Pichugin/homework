package my.hw5;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DeadlockExamples {
    
    public static void main(String[] args) {
        System.out.println("=== Deadlock Examples ===");
        System.out.println("Running 3 different deadlock scenarios...\n");
        

        System.out.println("Example 1: Synchronized Deadlock");
        runSynchronizedDeadlock();
        

        System.out.println("\nExample 2: ReentrantLock Deadlock");
        runReentrantLockDeadlock();
        

        System.out.println("\nExample 3: Nested Monitor Deadlock");
        runNestedMonitorDeadlock();
        
        System.out.println("\nAll deadlock examples completed.");
    }
    

    private static void runSynchronizedDeadlock() {
        final Object lock1 = new Object();
        final Object lock2 = new Object();
        
        Thread thread1 = new Thread(() -> {
            synchronized (lock1) {
                System.out.println("Thread 1: Acquired lock1, waiting for lock2...");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                synchronized (lock2) {
                    System.out.println("Thread 1: Acquired both locks!");
                }
            }
        });
        
        Thread thread2 = new Thread(() -> {
            synchronized (lock2) {
                System.out.println("Thread 2: Acquired lock2, waiting for lock1...");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                synchronized (lock1) {
                    System.out.println("Thread 2: Acquired both locks!");
                }
            }
        });
        
        thread1.start();
        thread2.start();
        
        try {
            Thread.sleep(2000);
            thread1.interrupt();
            thread2.interrupt();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    

    private static void runReentrantLockDeadlock() {
        final Lock lock1 = new ReentrantLock();
        final Lock lock2 = new ReentrantLock();
        
        Thread thread1 = new Thread(() -> {
            lock1.lock();
            System.out.println("Thread 1: Acquired lock1, waiting for lock2...");
            try {
                Thread.sleep(100);
                lock2.lock();
                System.out.println("Thread 1: Acquired both locks!");
                lock2.unlock();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock1.unlock();
            }
        });
        
        Thread thread2 = new Thread(() -> {
            lock2.lock();
            System.out.println("Thread 2: Acquired lock2, waiting for lock1...");
            try {
                Thread.sleep(100);
                lock1.lock();
                System.out.println("Thread 2: Acquired both locks!");
                lock1.unlock();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock2.unlock();
            }
        });
        
        thread1.start();
        thread2.start();
        
        try {
            Thread.sleep(2000);
            thread1.interrupt();
            thread2.interrupt();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    

    private static void runNestedMonitorDeadlock() {
        final Object outerLock = new Object();
        final Object innerLock = new Object();
        
        Thread thread1 = new Thread(() -> {
            synchronized (outerLock) {
                System.out.println("Thread 1: Acquired outer lock, waiting for inner lock...");
                try {
                    Thread.sleep(100);
                    synchronized (innerLock) {
                        System.out.println("Thread 1: Acquired both locks!");
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        
        Thread thread2 = new Thread(() -> {
            synchronized (innerLock) {
                System.out.println("Thread 2: Acquired inner lock, waiting for outer lock...");
                try {
                    Thread.sleep(100);
                    synchronized (outerLock) {
                        System.out.println("Thread 2: Acquired both locks!");
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        
        thread1.start();
        thread2.start();
        
        try {
            Thread.sleep(2000);
            thread1.interrupt();
            thread2.interrupt();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
