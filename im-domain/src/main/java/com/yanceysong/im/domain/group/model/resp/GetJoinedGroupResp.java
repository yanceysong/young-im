package com.yanceysong.im.domain.group.model.resp;

import com.yanceysong.im.domain.group.dao.ImGroupEntity;
import lombok.Data;

import java.util.List;

/**
 * @ClassName GetJoinedGroupResp
 * @Description
 * @date 2023/5/5 11:53
 * @Author yanceysong
 * @Version 1.0
 */
@Data
public class GetJoinedGroupResp {

    private Integer totalCount;

    private List<ImGroupEntity> groupList;

}