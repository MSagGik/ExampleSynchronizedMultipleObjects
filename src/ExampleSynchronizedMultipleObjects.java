import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ExampleSynchronizedMultipleObjects {
    public static void main(String[] args) {
        new Worker().main();
    }
}
class Worker {
    // объект рандомных значений
    Random random = new Random();
    private List<Integer> list1 = new ArrayList<>();
    private List<Integer> list2 = new ArrayList<>();

    // объекты для параллельной синхронизации
    // в данном случае addToList1() и addToList2() выполняются одновременно
    // поочерёдно разными потоками
    Object lock1 = new Object();
    Object lock2 = new Object();
    public void addToList1() {
        // синхронизация на объекте lock1 (мониторе lock1)
        synchronized (lock1) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            list1.add(random.nextInt(100));
        }
    }
    public synchronized void addToList2() {
        // синхронизация на объекте lock2 (мониторе lock2)
        synchronized (lock2) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            list2.add(random.nextInt(100));
        }
    }
    public void work() {
        for (int i = 0; i < 1000; i++) {
            addToList1();
            addToList2();
        }
    }
    public void main() {
        long before = System.currentTimeMillis();

        // создание потоков
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                work();
            }
        });

        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                work();
            }
        });

        // запуск потоков
        thread1.start();
        thread2.start();

        // ожидание выполнения потоков
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        long after = System.currentTimeMillis();
        System.out.println("Program took " + (after - before) + " ms to run");

        System.out.println("List1: " + list1.size());
        System.out.println("List2: " + list2.size());
    }
}
