package my.hw5;

import java.math.BigInteger;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Bank {
    private final int numberOfAccounts;
    private final long[] balances;
    private final Random random = new Random();

    private final ReentrantLock[] accountLocks;

    private final AtomicLong[] atomicBalances;

    private final ReadWriteLock[] readWriteLocks;
    
    public Bank(int numberOfAccounts, long minBalance, long maxBalance) {
        this.numberOfAccounts = numberOfAccounts;
        this.balances = new long[numberOfAccounts];
        this.accountLocks = new ReentrantLock[numberOfAccounts];
        this.atomicBalances = new AtomicLong[numberOfAccounts];
        this.readWriteLocks = new ReadWriteLock[numberOfAccounts];
        
        for (int i = 0; i < numberOfAccounts; i++) {
            balances[i] = minBalance + random.nextLong(maxBalance - minBalance + 1);
            accountLocks[i] = new ReentrantLock();
            atomicBalances[i] = new AtomicLong(balances[i]);
            readWriteLocks[i] = new ReentrantReadWriteLock();
        }
    }
    
    public int pickRandomAccountId() {
        return random.nextInt(numberOfAccounts);
    }

    public synchronized long getAccountBalance(int accountId) {
        validateAccountId(accountId);
        return balances[accountId];
    }
    
    public synchronized void setAccountBalance(int accountId, long newBalance) {
        validateAccountId(accountId);
        balances[accountId] = newBalance;
    }

    public long getAccountBalanceWithLock(int accountId) {
        validateAccountId(accountId);
        ReentrantLock lock = accountLocks[accountId];
        lock.lock();
        try {
            return balances[accountId];
        } finally {
            lock.unlock();
        }
    }
    
    public void setAccountBalanceWithLock(int accountId, long newBalance) {
        validateAccountId(accountId);
        ReentrantLock lock = accountLocks[accountId];
        lock.lock();
        try {
            balances[accountId] = newBalance;
        } finally {
            lock.unlock();
        }
    }

    public long getAccountBalanceAtomic(int accountId) {
        validateAccountId(accountId);
        return atomicBalances[accountId].get();
    }
    
    public void setAccountBalanceAtomic(int accountId, long newBalance) {
        validateAccountId(accountId);
        atomicBalances[accountId].set(newBalance);
    }

    public long getAccountBalanceReadWriteLock(int accountId) {
        validateAccountId(accountId);
        ReadWriteLock lock = readWriteLocks[accountId];
        lock.readLock().lock();
        try {
            return balances[accountId];
        } finally {
            lock.readLock().unlock();
        }
    }
    
    public void setAccountBalanceReadWriteLock(int accountId, long newBalance) {
        validateAccountId(accountId);
        ReadWriteLock lock = readWriteLocks[accountId];
        lock.writeLock().lock();
        try {
            balances[accountId] = newBalance;
        } finally {
            lock.writeLock().unlock();
        }
    }
    

    public synchronized boolean transferSynchronized(int from, int to, long amount) {
        validateAccountId(from);
        validateAccountId(to);
        
        if (balances[from] < amount) {
            return false;
        }
        
        balances[from] -= amount;
        balances[to] += amount;
        return true;
    }
    

    public boolean transferWithLock(int from, int to, long amount) {
        validateAccountId(from);
        validateAccountId(to);

        ReentrantLock firstLock = from < to ? accountLocks[from] : accountLocks[to];
        ReentrantLock secondLock = from < to ? accountLocks[to] : accountLocks[from];
        
        firstLock.lock();
        try {
            secondLock.lock();
            try {
                if (balances[from] < amount) {
                    return false;
                }
                
                balances[from] -= amount;
                balances[to] += amount;
                return true;
            } finally {
                secondLock.unlock();
            }
        } finally {
            firstLock.unlock();
        }
    }

    public boolean transferAtomic(int from, int to, long amount) {
        validateAccountId(from);
        validateAccountId(to);
        
        while (true) {
            long currentFrom = atomicBalances[from].get();
            if (currentFrom < amount) {
                return false;
            }
            
            if (atomicBalances[from].compareAndSet(currentFrom, currentFrom - amount)) {
                atomicBalances[to].addAndGet(amount);
                return true;
            }
        }
    }
    
    public BigInteger getSumOfAllAccounts() {
        BigInteger sum = BigInteger.ZERO;
        for (long balance : balances) {
            sum = sum.add(BigInteger.valueOf(balance));
        }
        return sum;
    }
    
    private void validateAccountId(int accountId) {
        if (accountId < 0 || accountId >= numberOfAccounts) {
            throw new IllegalArgumentException("Invalid account ID: " + accountId);
        }
    }
}
