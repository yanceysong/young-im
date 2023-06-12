package com.yanceysong.im.domain.friendship.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @ClassName ImFriendShipRequestEntity
 * @Description
 * @date 2023/5/5 10:46
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@TableName("im_friendship_request")
@ToString(doNotUseGetters=true)
@EqualsAndHashCode(callSuper= false)
public class ImFriendShipRequestEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Integer appId;

    private String sendId;

    private String receiverId;
    /** 备注*/
//    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String remark;

    //是否已读 1已读
    private Integer readStatus;

    /** 好友来源*/
//    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String addSource;

    //    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String addWording;

    //审批状态 1同意 2拒绝
    private Integer approveStatus;

    //    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long createTime;

    private Long updateTime;

    /** 序列号*/
    private Long sequence;



}
