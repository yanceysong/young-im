package com.yanceysong.im.domain.user.model.resp;

import com.yanceysong.im.domain.user.dao.ImUserDataEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @ClassName GetUserInfoResp
 * @Description
 * @date 2023/5/5 11:25
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@AllArgsConstructor
public class GetUserInfoResp {
    private List<ImUserDataEntity> userDataItem;

    private List<String> failUser;

}
