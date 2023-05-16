package com.yanceysong.im.common.thradPool;

import java.util.concurrent.*;

/**
 * @ClassName ThreadPoolFactory
 * @Description todo 后续改成动态线程池
 * @date 2022/8/9 9:12
 * @Author yanceysong
 * @Version 1.0
 */
public class ThreadPoolFactory {
    //核心线程数量
    private static final int coreThread = Math.max(1, Runtime.getRuntime().availableProcessors());
    //最大线程数量
    private static final int maxThread = Math.max(2, Runtime.getRuntime().availableProcessors() * 2 + 1);
    //线程存活时间
    private static final long aliveTime = 30;
    //存活时间单位
    private static final TimeUnit timeUnit = TimeUnit.SECONDS;
    //阻塞队列长度
    private static final int blockQueueSize = 1000;
    //阻塞队列，用的数组
    private static final BlockingQueue<Runnable> blockQueue = new ArrayBlockingQueue<>(blockQueueSize);
    private static final ConcurrentHashMap<String, ThreadPoolExecutor> threadPool = new ConcurrentHashMap<>();
    private static final Object PUT_LOCK = new Object();

    private ThreadPoolFactory() {
        synchronized (ThreadPoolFactory.class) {
            if (threadPool.size() > 0) {
                throw new RuntimeException("请不要用反射破坏单例模式");
            }
        }
    }

    /**
     * 单例获取线程池
     *
     * @return 线程池
     */
    public static ExecutorService getThreadPool(String threadFutureName, Boolean daemon) {
        if (!threadPool.containsKey(threadFutureName)) {
            //不影响其他拿数据
            synchronized (PUT_LOCK) {
                if (!threadPool.containsKey(threadFutureName)) {
                    threadPool.put(threadFutureName,
                            new ThreadPoolExecutor(coreThread,
                                    maxThread,
                                    aliveTime,
                                    timeUnit,
                                    blockQueue,
                                    new YoungImThreadFactory(threadFutureName, daemon),
                                    new ThreadPoolExecutor.CallerRunsPolicy()));
                    //允许核心线程会被销毁
//                    thread_pool.allowCoreThreadTimeOut(true);
                }
            }
        }
        return threadPool.get(threadFutureName);
    }

    /**
     * 关闭线程池
     */
    public static void shutdownAllThreadPool() {
        threadPool.forEach((key, value) -> threadPool.remove(key).shutdown());
    }

    /**
     * 初始化线程池，刚加载的时候就把所有的线程都创建好
     */
    public static void initThreadPool() {
        try {
            ThreadPoolFactory.getThreadPool("system", false).submit(() -> {
                System.out.println("线程池初始化成功");
                return "ok";
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
