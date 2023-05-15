package com.yanceysong.im.codec.proto;

import com.yanceysong.im.common.enums.message.MessageType;
import lombok.Data;

/**
 * @ClassName MessageHeader
 * @Description +------------------------------------------------------+
 * | 指令 4byte     | 协议版本号 4byte  | 消息解析类型 4byte  |
 * +------------------------------------------------------+
 * | 设备类型 4byte  | 设备号长度 4byte  | 平台ID 4byte      |
 * +------------------------------------------------------+
 * | 数据长度 4byte  | 数据内容(设备号 imei 4byte + 请求体)   |
 * +------------------------------------------------------+
 * @date 2023/4/24 16:31
 * @Author yanceysong
 * @Version 1.0
 */
@Data
public class MessageHeader {

    /**
     * 消息操作指令(长度为4字节) 十六进制 一个消息的开始通常以0x开头
     */
    private Integer command;
    /**
     * 4字节 版本号
     */
    private Integer version;
    /**
     * 4字节 端类型
     */
    private Integer clientType;
    /**
     * 应用ID(4字节)
     */
    private Integer appId;

    /**
     * 数据解析类型(4字节) 和具体业务无关
     * 后续根据解析类型解析data数据 0x0:Json,0x1:ProtoBuf,0x2:Xml,默认:0x0
     */
    private Integer messageType = MessageType.DATA_TYPE_JSON.getCode();

    /**
     * 4字节 imei长度
     */
    private Integer imeiLength;

    /**
     * 4字节 包体长度
     */
    private int length;

    /**
     * imei号
     */
    private String imei;
}
