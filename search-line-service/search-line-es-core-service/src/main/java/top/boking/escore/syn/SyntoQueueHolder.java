package top.boking.escore.syn;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class SyntoQueueHolder<T> {
    private final ArrayBlockingQueue<T> cache = new ArrayBlockingQueue<>(20000);

    public boolean put(List<T> list, int time) {
        //计算剩余空间，如果剩余空间不足则等待10s，尝试重新插入
        if (!ensureCapacity(list.size())) {
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (ensureCapacity(list.size())) {
            return cache.addAll(list);
        }

        return false;

    }

    public T poll() {
        return cache.poll();
    }

    public T poll(long timeout) {
        try {
            return cache.poll(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean ensureCapacity(int putSize) {
        return cache.remainingCapacity() > putSize;
    }

}
