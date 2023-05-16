package com.yanceysong.im.message.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yanceysong.im.message.dao.ImGroupMessageHistoryEntity;
import org.springframework.stereotype.Repository;

/**
 * @ClassName ImGroupMessageHistoryMapper
 * @Description
 * @date 2023/5/16 11:13
 * @Author yanceysong
 * @Version 1.0
 */
@Repository("groupMessageHistoryMapper")
public interface ImGroupMessageHistoryMapper extends BaseMapper<ImGroupMessageHistoryEntity> {

}
