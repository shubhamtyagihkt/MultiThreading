package Java.BlockingQueue;

/*
 * This is example of Blocking Queue / Bounded Buffer / Publisher Subscriber
 */
class BlockingQueue<T> {
    T[] array;
    int size;
    int capacity;
    int head = 0;
    int tail = 0;

    @SuppressWarnings("unchecked")
    public BlockingQueue(int capacity) {
        this.capacity = capacity;
        array = (T[]) new Object[capacity];
    }

    public synchronized void enqueue(T item) throws InterruptedException {
        while (size == capacity) {
            wait();
        }
        array[tail%capacity] = item;
        tail = (tail + 1) % capacity;
        size++;

        notifyAll();
    }

    public synchronized T dequeue() throws InterruptedException{
        if (size == 0) {
            wait();
        }
        T val = array[head];
        array[head] = null;
        head = (head + 1) % capacity;
        size--;

        notifyAll();
        return val;
    }
}


class main {
    public static void main(String[] args) throws InterruptedException {
        final BlockingQueue<Integer> q = new BlockingQueue<Integer>(5);

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
