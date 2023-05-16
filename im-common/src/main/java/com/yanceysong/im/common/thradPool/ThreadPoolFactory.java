package com.yanceysong.im.common.thradPool;

import java.util.concurrent.*;

/**
 * @ClassName ThreadPoolFactory
 * @Description todo 后续改成动态线程池，并且线程增加线程名字
 * @date 2022/8/9 9:12
 * @Author yanceysong
 * @Version 1.0
 */
public class ThreadPoolFactory {
    //核心线程数量
    private static final int coreThread = Math.max(1, Runtime.getRuntime().availableProcessors());
    //    private static final int coreThread = 100;
    //最大线程数量
//    private static final int maxThread = 200;
    private static final int maxThread = Math.max(2, Runtime.getRuntime().availableProcessors() * 2 + 1);
    //线程存活时间
    private static final long aliveTime = 2;
    //存活时间单位
    private static final TimeUnit timeUnit = TimeUnit.HOURS;
    //阻塞队列长度
    private static final int blockQueueSize = 30;
    //阻塞队列，用的数组
    private static final BlockingQueue<Runnable> blockQueue = new ArrayBlockingQueue<>(blockQueueSize);
    private static volatile ThreadPoolExecutor threadPool = null;

    private ThreadPoolFactory() {
        synchronized (ThreadPoolFactory.class) {
            if (threadPool != null) {
                throw new RuntimeException("请不要用反射破坏单例模式");
            }
        }
    }

    /**
     * 单例获取线程池
     *
     * @return 线程池
     */
    public static ExecutorService getThreadPool(String threadFutureName) {
        if (threadPool == null) {
            synchronized (ThreadPoolFactory.class) {
                if (threadPool == null) {
                    threadPool = new ThreadPoolExecutor(coreThread
                            , maxThread
                            , aliveTime
                            , timeUnit
                            , blockQueue
                            , Executors.defaultThreadFactory()
                            , new ThreadPoolExecutor.CallerRunsPolicy()
                    );
                    //允许核心线程会被销毁
//                    thread_pool.allowCoreThreadTimeOut(true);
                }
            }
        }
        return threadPool;
    }

    /**
     * 关闭线程池
     */
    public static void shutdown_thread_pool() {
        threadPool.shutdown();
        threadPool = null;//gc
    }

    /**
     * 初始化线程池，刚加载的时候就把所有的线程都创建好
     */
    public static void initThreadPool() {
        try {
            ThreadPoolFactory.getThreadPool("system").submit(() -> {
                System.out.println("线程池初始化成功");
                return "ok";
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
