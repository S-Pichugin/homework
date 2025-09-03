package my.hw5;

import java.math.BigInteger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BankingSimulator {
    private static final int ACCOUNT_COUNT = 200;
    private static final long MIN_BALANCE = 0L;
    private static final long MAX_BALANCE = 1_000L;
    private static final int THREAD_COUNT = 1_000;
    private static final int TRANSFERS_PER_THREAD = 100;
    
    public static void main(String[] args) {
        System.out.println("=== Banking Simulator ===");
        System.out.println("Accounts: " + ACCOUNT_COUNT);
        System.out.println("Threads: " + THREAD_COUNT);
        System.out.println("Transfers per thread: " + TRANSFERS_PER_THREAD);
        System.out.println();

        testSynchronizedVersion();

        testReentrantLockVersion();

        testAtomicVersion();
    }
    
    private static void testSynchronizedVersion() {
        System.out.println("--- Testing Synchronized Version ---");
        Bank bank = new Bank(ACCOUNT_COUNT, MIN_BALANCE, MAX_BALANCE);
        
        BigInteger initialTotal = bank.getSumOfAllAccounts();
        System.out.println("Initial total: " + initialTotal);
        
        long startTime = System.currentTimeMillis();
        
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < THREAD_COUNT; i++) {
                executor.submit(() -> {
                    for (int j = 0; j < TRANSFERS_PER_THREAD; j++) {
                        int from = bank.pickRandomAccountId();
                        int to = bank.pickRandomAccountId();
                        
                        if (from != to) {
                            long fromBalance = bank.getAccountBalance(from);
                            long amount = (long) (Math.random() * fromBalance);
                            
                            if (amount > 0) {
                                bank.transferSynchronized(from, to, amount);
                            }
                        }
                    }
                });
            }
            
            executor.shutdown();
            executor.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Synchronized test interrupted");
        }
        
        long endTime = System.currentTimeMillis();
        BigInteger finalTotal = bank.getSumOfAllAccounts();
        
        System.out.println("Final total: " + finalTotal);
        System.out.println("Execution time: " + (endTime - startTime) + " ms");
        System.out.println("Total preserved: " + (initialTotal.equals(finalTotal)));
        System.out.println();
    }
    
    private static void testReentrantLockVersion() {
        System.out.println("--- Testing ReentrantLock Version ---");
        Bank bank = new Bank(ACCOUNT_COUNT, MIN_BALANCE, MAX_BALANCE);
        
        BigInteger initialTotal = bank.getSumOfAllAccounts();
        System.out.println("Initial total: " + initialTotal);
        
        long startTime = System.currentTimeMillis();
        
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < THREAD_COUNT; i++) {
                executor.submit(() -> {
                    for (int j = 0; j < TRANSFERS_PER_THREAD; j++) {
                        int from = bank.pickRandomAccountId();
                        int to = bank.pickRandomAccountId();
                        
                        if (from != to) {
                            long fromBalance = bank.getAccountBalanceWithLock(from);
                            long amount = (long) (Math.random() * fromBalance);
                            
                            if (amount > 0) {
                                bank.transferWithLock(from, to, amount);
                            }
                        }
                    }
                });
            }
            
            executor.shutdown();
            executor.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("ReentrantLock test interrupted");
        }
        
        long endTime = System.currentTimeMillis();
        BigInteger finalTotal = bank.getSumOfAllAccounts();
        
        System.out.println("Final total: " + finalTotal);
        System.out.println("Execution time: " + (endTime - startTime) + " ms");
        System.out.println("Total preserved: " + (initialTotal.equals(finalTotal)));
        System.out.println();
    }
    
    private static void testAtomicVersion() {
        System.out.println("--- Testing Atomic Version ---");
        Bank bank = new Bank(ACCOUNT_COUNT, MIN_BALANCE, MAX_BALANCE);
        
        BigInteger initialTotal = bank.getSumOfAllAccounts();
        System.out.println("Initial total: " + initialTotal);
        
        long startTime = System.currentTimeMillis();
        
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < THREAD_COUNT; i++) {
                executor.submit(() -> {
                    for (int j = 0; j < TRANSFERS_PER_THREAD; j++) {
                        int from = bank.pickRandomAccountId();
                        int to = bank.pickRandomAccountId();
                        
                        if (from != to) {
                            long fromBalance = bank.getAccountBalanceAtomic(from);
                            long amount = (long) (Math.random() * fromBalance);
                            
                            if (amount > 0) {
                                bank.transferAtomic(from, to, amount);
                            }
                        }
                    }
                });
            }
            
            executor.shutdown();
            executor.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Atomic test interrupted");
        }
        
        long endTime = System.currentTimeMillis();
        BigInteger finalTotal = bank.getSumOfAllAccounts();
        
        System.out.println("Final total: " + finalTotal);
        System.out.println("Execution time: " + (endTime - startTime) + " ms");
        System.out.println("Total preserved: " + (initialTotal.equals(finalTotal)));
        System.out.println();
    }
}
