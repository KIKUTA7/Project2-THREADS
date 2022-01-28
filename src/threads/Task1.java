package threads;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Task1 {
    private static final int NUM_THREADS = 10;

    public synchronized static List<String> generate(final int from, final int to, final int count) throws InterruptedException {
        if (from < 0 || to < 0 || !isInRange(count, 0, to - from + 1)) throw new IllegalArgumentException();
        AtomicInteger count2 = new AtomicInteger();
        List<String> generated = new ArrayList<>(count);
        Runnable r = new Runnable() {
            @Override
            public synchronized void run() {
                while (count2.get() < count) {
                    int l = (int) (Math.random() * (from + Math.random() * (to - from + 1)));
                    String s = KanjiLib.convert(l);
                    if (!generated.contains(s) && count2.get() <= count) {
                        generated.add(l + ", " + s);
                        count2.getAndIncrement();
                    } else {
                        if (count2.get() > count)
                            Thread.currentThread().interrupt();
                    }
                }
            }
        };

        Thread thr [] = new Thread[NUM_THREADS];
        for (int i=0;i< NUM_THREADS;i++)
        {
            thr[i] = new Thread(r);


        }
        for (Thread th: thr)
        {
            th.start();
        }
        for (Thread th : thr)
        {
            th.join();
        }

        return generated;
    }

    private static boolean isInRange(int count, int from, int to) {
        return from <= count && count <= to;
    }
}