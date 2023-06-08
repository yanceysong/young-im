package com.yanceysong.im.domain.message.strategy.model;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @ClassName DomainCommandContext
 * @Description
 * @date 2023/6/8 17:04
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@AllArgsConstructor
public class DomainCommandContext {
    private String msg;
    private JSONObject json;
}
