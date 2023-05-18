package com.yanceysong.im.domain.group.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yanceysong.im.domain.group.dao.ImGroupEntity;
import org.springframework.stereotype.Repository;

import java.util.Collection;

/**
 * @ClassName ImGroupMapper
 * @Description
 * @date 2023/5/5 11:48
 * @Author yanceysong
 * @Version 1.0
 */
@Repository
public interface ImGroupMapper extends BaseMapper<ImGroupEntity> {

    /**
     * 增量拉取用户被拉入群组通知列表中最大的序列号
     * @param data
     * @param appId
     * @return
     */
    Long getJoinGroupMaxSeq(Collection<String> data, Integer appId);

}
