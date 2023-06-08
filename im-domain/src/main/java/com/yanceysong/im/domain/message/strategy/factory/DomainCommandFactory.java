package com.yanceysong.im.domain.message.strategy.factory;

import com.yanceysong.im.domain.message.strategy.DomainCommandStrategy;
import org.springframework.stereotype.Service;

/**
 * @ClassName DomainCommandFactory
 * @Description
 * @date 2023/6/8 16:52
 * @Author yanceysong
 * @Version 1.0
 */
@Service
public class DomainCommandFactory extends DomainCommandFactoryConfig{
    public DomainCommandStrategy getStrategy(Integer command) {
        return commandStrategyMap.get(command);
    }
}
