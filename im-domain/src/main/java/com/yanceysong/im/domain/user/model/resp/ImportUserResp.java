package com.yanceysong.im.domain.user.model.resp;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @ClassName ImportUserResp
 * @Description
 * @date 2023/5/5 11:25
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@AllArgsConstructor
public class ImportUserResp {

    private List<String> successId;

    private List<String> errorId;

}

