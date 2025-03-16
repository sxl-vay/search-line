package top.boking.escore.syn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.boking.escore.syn.dao.CommentDao;
import top.boking.escore.syn.dao.PushDao;
import top.boking.escore.syn.entity.PushBashEntity;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class Syn<T extends PushBashEntity> {

    private static final Logger log = LoggerFactory.getLogger(CommentDao.class);

    private final SyntoQueueHolder<T> syntoQueueHolder = new SyntoQueueHolder<>();

    private final AtomicBoolean isFinish = new AtomicBoolean(false);
    private final Consumer<T> consumer;
    private final PushDao<T> pushDao;
    private AtomicLong count = new AtomicLong(0);

    public Syn(Consumer<T> consumer, PushDao<T> pushDao) {

        this.consumer = consumer;
        this.pushDao = pushDao;

    }

    public long getConsumerCount() {
        return count.get();
    }

    public boolean isFinish() {
        return isFinish.get();
    }

    public void startPoll() {
        try {
            CompletableFuture.runAsync(() -> {
                if (isFinish.get()) {
                    return;
                }
                List<T> comments = pushDao.batchQueryComments(0, 1000);

                while (comments.size() > 0) {
                    boolean put = syntoQueueHolder.put(comments, 1000);
                    while (!put) {
                        syntoQueueHolder.put(comments, 1000);
                    }
                    comments = pushDao.batchQueryComments(comments.get(comments.size() - 1).getId(), 1000);
                }
                isFinish.set(true);
            });

            CompletableFuture.runAsync(() -> {
                T comment = syntoQueueHolder.poll();

                while (comment != null || !isFinish.get()) {
                    comment = syntoQueueHolder.poll(10);
                    if (comment != null) {
                        long c = count.incrementAndGet();
                        try {
                            consumer.accept(comment);
                        } catch (Exception e) {
                            log.error("consumer error:", e);
                        }
                        if (log.isTraceEnabled()) {
                            log.trace("consumer count:{}", c);
                        }
                    }
                }
                //获取 T 的实际对象名称
                String className = pushDao.getClass().getGenericInterfaces()[0].getTypeName();
                log.info("stopPoll:obj:{}, count:{}", className, count.get());
            });
            log.info("startPoll");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
