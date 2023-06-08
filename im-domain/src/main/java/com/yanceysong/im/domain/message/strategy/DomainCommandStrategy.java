package com.yanceysong.im.domain.message.strategy;

import com.yanceysong.im.domain.message.strategy.model.DomainCommandContext;

/**
 * @ClassName DomainCommandStratege
 * @Description
 * @date 2023/6/8 16:49
 * @Author yanceysong
 * @Version 1.0
 */
public interface DomainCommandStrategy {
    /**
     * 领域业务命令执行策略接口
     *
     * @param domainCommandContext 执行指令需要的内容
     */
    void doStrategy(DomainCommandContext domainCommandContext);

}
