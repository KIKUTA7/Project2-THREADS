package threads;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Task3 {
    private static final int NUM_THREADS = 10;
    private static final int CHANNEL_CAPACITY = 100;
    private static final int POISON_PILL = -1;
    private Thread [] thr = new Thread[NUM_THREADS];
    private Thread thr2 = new Thread();
    private List<String> generated = new ArrayList<>();

    public synchronized List<String> get() throws InterruptedException {
          try
          {
              thr2.join();
              for(Thread th: thr)
              {
                 th.join();
              }
          }
          catch (InterruptedException e)
          {
              e.printStackTrace();
          }
          return generated;
    }

    public synchronized List<Thread> getThreads() {
        List<Thread> thr3 = new ArrayList<>(10);
        thr3.addAll(Arrays.asList(thr));
        return thr3;

    }
    public synchronized void interrupt() {
        List<Thread> thr3 = this.getThreads();
        thr3.add(thr2);
        for (int i=0;i < thr3.size();i++)
        {
             if(!thr3.get(i).isInterrupted()) thr3.get(i).interrupt();
        }
    }

    public Task3(final int from, final int to, final int count) {
        if (from < 0 || to < 0 || !isInRange(count, 0, to - from + 1)) throw new IllegalArgumentException();

         generated = new ArrayList<>(count);
        AtomicInteger at = new AtomicInteger();


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


                    if (at.get() == count) {
                        while (l.size() > 90) {
                            try {
                                wait();
                            } catch (InterruptedException e) {

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

        thr2 = new Thread(r2);
        Runnable r3 = new Runnable() {
            @Override
            public synchronized void run() {
                if(Thread.currentThread().isInterrupted()) interrupt();
                else {
                    while (l.size() > 0 && generated.size() < count) {
                        int i2 = l.remove(0);
                        if (i2 == POISON_PILL) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                        String s = KanjiLib.convert(i2);
                        generated.add(i2 + ", " + s);
                        if (generated.size() == count) {

                            Thread.currentThread().interrupt();
                        }
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


    }

    private static boolean isInRange(int count, int from, int to) {
        return from <= count && count <= to;
    }
}