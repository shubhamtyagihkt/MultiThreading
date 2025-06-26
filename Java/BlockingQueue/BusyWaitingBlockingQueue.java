package Java.BlockingQueue;

import java.util.concurrent.locks.Lock;

/*
 * This is example of Blocking Queue / Bounded Buffer / Publisher Subscriber
 */
class BlockingQueueWithMutex<T> {
    T[] array;
    int size;
    int capacity;
    int head = 0;
    int tail = 0;

    Lock lock;

    @SuppressWarnings("unchecked")
    public BlockingQueueWithMutex(int capacity) {
        this.capacity = capacity;
        array = (T[]) new Object[capacity];
    }

    public void enqueue(T item) throws InterruptedException {
        lock.lock();
        while (size == capacity) {
            lock.unlock();

            lock.lock();
        }
        array[tail%capacity] = item;
        tail = (tail + 1) % capacity;
        size++;

        lock.unlock();
    }

    public T dequeue() throws InterruptedException{
        lock.lock();
        if (size == 0) {
            lock.unlock();
            lock.lock();
        }
        T val = array[head];
        array[head] = null;
        head = (head + 1) % capacity;
        size--;

        lock.unlock();
        return val;
    }
}


class main {
    public static void main(String[] args) throws InterruptedException {
        final BlockingQueueWithMutex<Integer> q = new BlockingQueueWithMutex<Integer>(5);

        Thread t1 = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    for (int i = 0; i < 50; i++) {
                        q.enqueue(i);
                        System.out.println("enqueued " + i);
                    }
                } catch (InterruptedException ie) {

                }
            }
        });

        Thread t2 = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    for (int i = 0; i < 25; i++) {
                        System.out.println("Thread 2 dequeued: " + q.dequeue());
                    }
                } catch (InterruptedException ie) {

                }
            }
        });

        Thread t3 = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    for (int i = 0; i < 25; i++) {
                        System.out.println("Thread 3 dequeued: " + q.dequeue());
                    }
                } catch (InterruptedException ie) {

                }
            }
        });

        t1.start();
        Thread.sleep(4000);
        t2.start();

        t2.join();

        t3.start();

    }
}
