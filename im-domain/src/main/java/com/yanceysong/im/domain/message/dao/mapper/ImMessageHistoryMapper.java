package com.yanceysong.im.domain.message.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yanceysong.im.domain.message.dao.ImMessageHistoryEntity;
import org.springframework.stereotype.Repository;

import java.util.Collection;

/**
 * @ClassName ImMessageHistoryMapper
 * @Description
 * @date 2023/5/16 10:36
 * @Author yanceysong
 * @Version 1.0
 */
@Repository
public interface ImMessageHistoryMapper extends BaseMapper<ImMessageHistoryEntity> {

    /**
     * 批量插入（mysql）
     * @param entityList
     * @return
     */
    Integer insertBatchSomeColumn(Collection<ImMessageHistoryEntity> entityList);

}

