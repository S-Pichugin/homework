package my.hw5;

import my.CustomList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ThreadSafeCustomList {

    public static class SynchronizedCustomList<E> implements List<E> {
        private final CustomList<E> list;

        public SynchronizedCustomList(CustomList<E> list) {
            this.list = list;
        }

        @Override
        public synchronized int size() { return list.size(); }

        @Override
        public synchronized boolean isEmpty() { return list.isEmpty(); }

        @Override
        public synchronized boolean contains(Object o) { return list.contains(o); }

        @Override
        public synchronized Iterator<E> iterator() { return list.iterator(); }

        @Override
        public synchronized Object[] toArray() { return list.toArray(); }

        @Override
        @SuppressWarnings("unchecked")
        public synchronized <T> T[] toArray(T[] a) { 
            return (T[]) list.toArray(a); 
        }

        @Override
        public synchronized boolean add(E e) { return list.add(e); }

        @Override
        public synchronized boolean remove(Object o) { return list.remove(o); }

        @Override
        public synchronized boolean containsAll(Collection<?> c) { return list.containsAll(c); }

        @Override
        public synchronized boolean addAll(Collection<? extends E> c) { return list.addAll(c); }

        @Override
        public synchronized boolean addAll(int index, Collection<? extends E> c) { return list.addAll(index, c); }

        @Override
        public synchronized boolean removeAll(Collection<?> c) { return list.removeAll(c); }

        @Override
        public synchronized boolean retainAll(Collection<?> c) { return list.retainAll(c); }

        @Override
        public synchronized void clear() { list.clear(); }

        @Override
        public synchronized E get(int index) { return list.get(index); }

        @Override
        public synchronized E set(int index, E element) { return list.set(index, element); }

        @Override
        public synchronized void add(int index, E element) { list.add(index, element); }

        @Override
        public synchronized E remove(int index) { return list.remove(index); }

        @Override
        public synchronized int indexOf(Object o) { return list.indexOf(o); }

        @Override
        public synchronized int lastIndexOf(Object o) { return list.indexOf(o); }

        @Override
        public synchronized ListIterator<E> listIterator() { return list.listIterator(); }

        @Override
        public synchronized ListIterator<E> listIterator(int index) { return list.listIterator(index); }

        @Override
        public synchronized List<E> subList(int fromIndex, int toIndex) { return list.subList(fromIndex, toIndex); }
    }

    // Декоратор с ReentrantReadWriteLock
    public static class ReadWriteLockCustomList<E> implements List<E> {
        private final CustomList<E> list;
        private final ReadWriteLock lock = new ReentrantReadWriteLock();

        public ReadWriteLockCustomList(CustomList<E> list) {
            this.list = list;
        }

        @Override
        public int size() {
            lock.readLock().lock();
            try {
                return list.size();
            } finally {
                lock.readLock().unlock();
            }
        }

        @Override
        public boolean isEmpty() {
            lock.readLock().lock();
            try {
                return list.isEmpty();
            } finally {
                lock.readLock().unlock();
            }
        }

        @Override
        public boolean contains(Object o) {
            lock.readLock().lock();
            try {
                return list.contains(o);
            } finally {
                lock.readLock().unlock();
            }
        }

        @Override
        public Iterator<E> iterator() {
            lock.readLock().lock();
            try {
                return list.iterator();
            } finally {
                lock.readLock().unlock();
            }
        }

        @Override
        public Object[] toArray() {
            lock.readLock().lock();
            try {
                return list.toArray();
            } finally {
                lock.readLock().unlock();
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T[] toArray(T[] a) {
            lock.readLock().lock();
            try {
                return (T[]) list.toArray(a);
            } finally {
                lock.readLock().unlock();
            }
        }

        @Override
        public boolean add(E e) {
            lock.writeLock().lock();
            try {
                return list.add(e);
            } finally {
                lock.writeLock().unlock();
            }
        }

        @Override
        public boolean remove(Object o) {
            lock.writeLock().lock();
            try {
                return list.remove(o);
            } finally {
                lock.writeLock().unlock();
            }
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            lock.readLock().lock();
            try {
                return list.containsAll(c);
            } finally {
                lock.readLock().unlock();
            }
        }

        @Override
        public boolean addAll(Collection<? extends E> c) {
            lock.writeLock().lock();
            try {
                return list.addAll(c);
            } finally {
                lock.writeLock().unlock();
            }
        }

        @Override
        public boolean addAll(int index, Collection<? extends E> c) {
            lock.writeLock().lock();
            try {
                return list.addAll(index, c);
            } finally {
                lock.writeLock().unlock();
            }
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            lock.writeLock().lock();
            try {
                return list.removeAll(c);
            } finally {
                lock.writeLock().unlock();
            }
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            lock.writeLock().lock();
            try {
                return list.retainAll(c);
            } finally {
                lock.writeLock().unlock();
            }
        }

        @Override
        public void clear() {
            lock.writeLock().lock();
            try {
                list.clear();
            } finally {
                lock.writeLock().unlock();
            }
        }

        @Override
        public E get(int index) {
            lock.readLock().lock();
            try {
                return list.get(index);
            } finally {
                lock.readLock().unlock();
            }
        }

        @Override
        public E set(int index, E element) {
            lock.writeLock().lock();
            try {
                return list.set(index, element);
            } finally {
                lock.writeLock().unlock();
            }
        }

        @Override
        public void add(int index, E element) {
            lock.writeLock().lock();
            try {
                list.add(index, element);
            } finally {
                lock.writeLock().unlock();
            }
        }

        @Override
        public E remove(int index) {
            lock.writeLock().lock();
            try {
                return list.remove(index);
            } finally {
                lock.writeLock().unlock();
            }
        }

        @Override
        public int indexOf(Object o) {
            lock.readLock().lock();
            try {
                return list.indexOf(o);
            } finally {
                lock.readLock().unlock();
            }
        }

        @Override
        public int lastIndexOf(Object o) {
            lock.readLock().lock();
            try {
                return list.indexOf(o);
            } finally {
                lock.readLock().unlock();
            }
        }

        @Override
        public ListIterator<E> listIterator() {
            lock.readLock().lock();
            try {
                return list.listIterator();
            } finally {
                lock.readLock().unlock();
            }
        }

        @Override
        public ListIterator<E> listIterator(int index) {
            lock.readLock().lock();
            try {
                return list.listIterator(index);
            } finally {
                lock.readLock().unlock();
            }
        }

        @Override
        public List<E> subList(int fromIndex, int toIndex) {
            lock.readLock().lock();
            try {
                return list.subList(fromIndex, toIndex);
            } finally {
                lock.readLock().unlock();
            }
        }
    }

    // Декоратор с CopyOnWrite
    public static class CopyOnWriteCustomList<E> implements List<E> {
        private final CustomList<E> list;
        private final Object lock = new Object();
        
        public CopyOnWriteCustomList(CustomList<E> originalList) {
            this.list = originalList;
        }
        
        // Делегируем все методы к CustomList с синхронизацией
        @Override
        public int size() { 
            synchronized (lock) { return list.size(); }
        }
        
        @Override
        public boolean isEmpty() { 
            synchronized (lock) { return list.isEmpty(); }
        }
        
        @Override
        public boolean contains(Object o) { 
            synchronized (lock) { return list.contains(o); }
        }
        
        @Override
        public Iterator<E> iterator() { 
            synchronized (lock) { return list.iterator(); }
        }
        
        @Override
        public Object[] toArray() { 
            synchronized (lock) { return list.toArray(); }
        }
        
        @Override
        @SuppressWarnings("unchecked")
        public <T> T[] toArray(T[] a) { 
            synchronized (lock) { return (T[]) list.toArray(a); }
        }
        
        @Override
        public boolean add(E e) { 
            synchronized (lock) { return list.add(e); }
        }
        
        @Override
        public boolean remove(Object o) { 
            synchronized (lock) { return list.remove(o); }
        }
        
        @Override
        public boolean containsAll(Collection<?> c) { 
            synchronized (lock) { return list.containsAll(c); }
        }
        
        @Override
        public boolean addAll(Collection<? extends E> c) { 
            synchronized (lock) { return list.addAll(c); }
        }
        
        @Override
        public boolean addAll(int index, Collection<? extends E> c) { 
            synchronized (lock) { return list.addAll(index, c); }
        }
        
        @Override
        public boolean removeAll(Collection<?> c) { 
            synchronized (lock) { return list.removeAll(c); }
        }
        
        @Override
        public boolean retainAll(Collection<?> c) { 
            synchronized (lock) { return list.retainAll(c); }
        }
        
        @Override
        public void clear() { 
            synchronized (lock) { list.clear(); }
        }
        
        @Override
        public E get(int index) { 
            synchronized (lock) { return list.get(index); }
        }
        
        @Override
        public E set(int index, E element) { 
            synchronized (lock) { return list.set(index, element); }
        }
        
        @Override
        public void add(int index, E element) { 
            synchronized (lock) { list.add(index, element); }
        }
        
        @Override
        public E remove(int index) { 
            synchronized (lock) { return list.remove(index); }
        }
        
        @Override
        public int indexOf(Object o) { 
            synchronized (lock) { return list.indexOf(o); }
        }
        
        @Override
        public int lastIndexOf(Object o) { 
            synchronized (lock) { return list.indexOf(o); }
        }
        
        @Override
        public ListIterator<E> listIterator() { 
            synchronized (lock) { return list.listIterator(); }
        }
        
        @Override
        public ListIterator<E> listIterator(int index) { 
            synchronized (lock) { return list.listIterator(index); }
        }
        
        @Override
        public List<E> subList(int fromIndex, int toIndex) { 
            synchronized (lock) { return list.subList(fromIndex, toIndex); }
        }
    }
}
