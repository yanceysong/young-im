package com.yanceysong.im.infrastructure.rabbitmq.process;

/**
 * @ClassName ProcessFactory
 * @Description
 * @date 2023/5/15 10:36
 * @Author yanceysong
 * @Version 1.0
 */
public class ProcessFactory {

    private static final BaseProcess DEFAULT_PROCESS;

    static {
        DEFAULT_PROCESS = new BaseProcess() {
            @Override
            public void processBefore() {

            }

            @Override
            public void processAfter() {

            }
        };
    }

    public static BaseProcess getMessageProcess(Integer command) {
        // TODO 简易策略模式，后期可自由调节使用什么策略
        return DEFAULT_PROCESS;
    }

}
