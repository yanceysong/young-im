package com.yanceysong.im.domain.user.model.req;

import com.yanceysong.im.common.model.common.RequestBase;
import com.yanceysong.im.domain.user.dao.ImUserDataEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * @ClassName ImportUserReq
 * @Description
 * @date 2023/5/5 11:23
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper=true)
@ApiModel(value = "导入用户信息请求对象", description = "")
@ToString(doNotUseGetters=true)
public class ImportUserReq extends RequestBase {
    private List<ImUserDataEntity> userData;
}