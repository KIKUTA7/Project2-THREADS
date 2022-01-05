package threads;

import java.util.List;
import java.util.Set;

public class Task3 {
    private static final int NUM_THREADS = 10;
    private static final int CHANNEL_CAPACITY = 100;
    private static final int POISON_PILL = -1;



    public List<String> get() throws InterruptedException {
        // TODO 
        return null;
    }

    public List<Thread> getThreads() {
        // TODO 
        return null;
    }

    public void interrupt() {
        // TODO 
    }

    public Task3(final int from, final int to, final int count) {
        if (from < 0 || to < 0 || !isInRange(count, 0, to - from + 1)) throw new IllegalArgumentException();

        // TODO 
    }

    private static boolean isInRange(int count, int from, int to) {
        return from <= count && count <= to;
    }
}