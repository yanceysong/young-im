package com.yanceysong.im.domain.friendship.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @ClassName ImFriendShipGroupEntity
 * @Description
 * @date 2023/5/5 10:45
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@TableName("im_friendship_group")
@ToString(doNotUseGetters=true)
@EqualsAndHashCode(callSuper= false)
public class ImFriendShipGroupEntity {

    @TableId(value = "group_id",type = IdType.AUTO)
    private Long groupId;

    private String sendId;

    private Integer appId;

    private String groupName;
    /** 备注*/
    private Long createTime;

    /** 备注*/
    private Long updateTime;

    /** 序列号*/
    private Long sequence;

    private int delFlag;


}
