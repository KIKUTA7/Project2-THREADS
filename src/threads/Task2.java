package threads;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Task2 {
    private static final int NUM_THREADS = 10;
    private static final int CHANNEL_CAPACITY = 100;
    private static final int POISON_PILL = -1;

    public static List<String> generate(final int from, final int to, final int count) throws InterruptedException {
        if (from < 0 || to < 0 || !isInRange(count, 0, to - from + 1)) throw new IllegalArgumentException();

        List<String> generated = new ArrayList<>(count);
        AtomicInteger at = new AtomicInteger();

        Thread thr [] = new Thread[NUM_THREADS];
        List<Integer> l = new LinkedList<Integer>(Collections.singleton(CHANNEL_CAPACITY));
        Runnable r2 = new Runnable() {
            @Override
            public  synchronized void run() {
                while (at.get() < count && l.size() < CHANNEL_CAPACITY) {
                    int i = (int) (from + (to - from + 1) * Math.random());
                    if (!l.contains(i)) {
                        l.add(i);
                        at.getAndIncrement();
                        notifyAll();
                    }


                    if (at.get() >= count) {
                        while (l.size() > 90) {
                            try {
                                wait();
                            }catch (InterruptedException e) {

                            }
                        }

                        for (int ti = 0; ti < NUM_THREADS; ti++) {
                            l.add(POISON_PILL);
                            notifyAll();
                        }
                        Thread.currentThread().interrupt();
                    }
                }

            }
        };

        Thread thr2 = new Thread(r2);
        Runnable r3 = new Runnable() {
            @Override
            public synchronized void run() {
                while(l.size() > 0 && generated.size() < count)
                {
                    int i2 = l.remove(0);
                    if(i2 == POISON_PILL) {Thread.currentThread().interrupt();break;}
                    String s = KanjiLib.convert(i2);
                    generated.add(i2+", "+s);
                    if(generated.size() == count)
                    {

                        Thread.currentThread().interrupt();
                    }
                }
            }
        };
        thr2.start();
        for (int i=0;i<NUM_THREADS;i++)
        {
            thr[i] = new Thread(r3);

        }
        for (int i=0;i<NUM_THREADS;i++)
        {
            thr[i].start();

        }
        try
        {

            for (Thread th: thr)
            {
               th.join();
            }
            thr2.join();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        return generated;
    }

    private static boolean isInRange(int count, int from, int to) {
        return from <= count && count <= to;
    }
}