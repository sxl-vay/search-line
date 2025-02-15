package top.boking.base.util;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SLineThreadPool {
    private static ThreadPoolExecutor defaultThreadPool;

    //懒汉式加载一个线程池
    public static ThreadPoolExecutor getDefaultThreadPool() {
        if (defaultThreadPool != null) {
            return defaultThreadPool;
        }
        synchronized (SLineThreadPool.class) {
            if (defaultThreadPool != null) {
               return defaultThreadPool;
            }
            //创建名称为 SLineDefaultThreadPool的线程池
            defaultThreadPool = new ThreadPoolExecutor(
                    //核心线程数
                    5,
                    //最大线程数
                    10,
                    //线程存活时间
                    60,
                    //线程存活时间单位
                    TimeUnit.SECONDS,
                    //阻塞队列
                    new ArrayBlockingQueue<>(100),
                    (e) -> new Thread(e, "SLineDefaultThreadPool"),
                    //线程工厂
                    new ThreadPoolExecutor.CallerRunsPolicy()
            );
        }
        return defaultThreadPool;
    }

}
