package com.yanceysong.im.domain.user.model.req;

import com.yanceysong.im.common.model.RequestBase;
import com.yanceysong.im.domain.user.dao.ImUserDataEntity;
import lombok.Data;

import java.util.List;

/**
 * @ClassName ImportUserReq
 * @Description
 * @date 2023/5/5 11:23
 * @Author yanceysong
 * @Version 1.0
 */
@Data
public class ImportUserReq extends RequestBase {

    private List<ImUserDataEntity> userData;


}