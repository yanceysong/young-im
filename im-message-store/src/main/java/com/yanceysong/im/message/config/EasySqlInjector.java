package com.yanceysong.im.message.config;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.extension.injector.methods.InsertBatchSomeColumn;

import java.util.List;

/**
 * @ClassName EasySqlInjector
 * @Description
 * @date 2023/5/16 11:03
 * @Author yanceysong
 * @Version 1.0
 */
public class EasySqlInjector extends DefaultSqlInjector {

    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass) {
        List<AbstractMethod> methodList = super.getMethodList(mapperClass);
        // 添加InsertBatchSomeColumn方法
        methodList.add(new InsertBatchSomeColumn());
        return methodList;
    }

}