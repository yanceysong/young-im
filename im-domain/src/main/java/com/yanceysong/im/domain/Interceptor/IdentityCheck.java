package com.yanceysong.im.domain.Interceptor;

import com.alibaba.fastjson.JSONObject;
import com.yanceysong.im.codec.util.SigApi;
import com.yanceysong.im.common.BaseErrorCode;
import com.yanceysong.im.common.ResponseVO;
import com.yanceysong.im.common.constant.RedisConstants;
import com.yanceysong.im.common.enums.error.GateWayErrorCode;
import com.yanceysong.im.common.enums.user.UserTypeEnum;
import com.yanceysong.im.common.exception.YoungImExceptionEnum;
import com.yanceysong.im.domain.user.dao.ImUserDataEntity;
import com.yanceysong.im.domain.user.service.ImUserService;
import com.yanceysong.im.infrastructure.config.AppConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName IdentityCheck
 * @Description
 * @date 2023/5/15 11:19
 * @Author yanceysong
 * @Version 1.0
 */
@Slf4j
@Component
public class IdentityCheck {

    @Resource
    private ImUserService userService;
    @Resource
    private RedisTemplate<String, String> stringRedisTemplate;
    // TODO 后期需要将配置文件升级到数据库表进行持久化
    @Resource
    private AppConfig appConfig;

    public YoungImExceptionEnum checkUserSign(String identifier, String appId, String userSig) {
        String key = appId + RedisConstants.USER_SIGN + identifier + userSig;
        // 10001:userSign:
        String cacheUserSig = stringRedisTemplate.opsForValue().get(key);
        if (!StringUtils.isBlank(cacheUserSig) &&
                Long.parseLong(cacheUserSig) > System.currentTimeMillis() / 1000) {
            this.setIsAdmin(identifier, Integer.valueOf(appId));
            return BaseErrorCode.SUCCESS;
        }
        //没有缓存就验证一下
        // 获取当前用户的密钥
        String privateKey = appConfig.getPrivateKey();
        // TODO 这一段逻辑需要更改，服务端生成密钥提供给客户端，而不是直接嵌套在这
        // 根据 appId + 密钥创建 sigApi(加密 token)
        SigApi sigApi = new SigApi(Long.parseLong(appId), privateKey);
        // 调用 sigApi 对 userSig 解密
        JSONObject jsonObject = SigApi.decodeUserSig(userSig);

        //取出解密后的 appid 和 操作人 和 过期时间做匹配，不通过则提示错误
        long expireTime = 0L;
        long expireSec = 0L;
        long time = 0L;
        String decoderAppId = "";
        String decoderIdentifier = "";
        try {
            decoderAppId = jsonObject.getString("TLS.appId");
            decoderIdentifier = jsonObject.getString("TLS.identifier");
            String expireStr = jsonObject.get("TLS.expire").toString();
            String expireTimeStr = jsonObject.get("TLS.expireTime").toString();
            time = Long.parseLong(expireTimeStr);
            expireSec = Long.parseLong(expireStr);
            expireTime = Long.parseLong(expireTimeStr) + expireSec;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("checkUserSig-error:{}", e.getMessage());
        }

        if (!decoderIdentifier.equals(identifier)) {
            return GateWayErrorCode.USER_SIGN_OPERATE_NOT_MATE;
        }

        if (!decoderAppId.equals(appId)) {
            return GateWayErrorCode.USER_SIGN_IS_ERROR;
        }

        if (expireSec == 0L) {
            return GateWayErrorCode.USER_SIGN_IS_EXPIRED;
        }

        if (expireTime < System.currentTimeMillis() / 1000) {
            return GateWayErrorCode.USER_SIGN_IS_EXPIRED;
        }

        //appid + "xxx" + userId + sign
        String genSig = sigApi.genUserSig(identifier, expireSec, time, null);
        if (genSig.equalsIgnoreCase(userSig)) {

            long etime = expireTime - System.currentTimeMillis() / 1000;
            stringRedisTemplate.opsForValue().set(key, Long.toString(expireTime), etime, TimeUnit.SECONDS);
            return BaseErrorCode.SUCCESS;
        }

        return GateWayErrorCode.USER_SIGN_IS_ERROR;
    }

    /**
     * 根据appid,identifier判断是否App管理员,并设置到RequestHolder
     *
     * @param identifier
     * @param appId
     * @return
     */
    public void setIsAdmin(String identifier, Integer appId) {
        //去DB或Redis中查找, 后面写
        ResponseVO singleUserInfo = userService.getSingleUserInfo(identifier, appId);
        if (singleUserInfo.isOk()) {
            ImUserDataEntity userInfo = (ImUserDataEntity) singleUserInfo.getData();
            RequestHolder.set(userInfo.getUserType() == UserTypeEnum.APP_ADMIN.getCode());
        } else {
            RequestHolder.set(false);
        }
    }
}

