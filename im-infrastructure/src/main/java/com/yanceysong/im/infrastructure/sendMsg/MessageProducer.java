package com.yanceysong.im.infrastructure.sendMsg;

import com.yanceysong.im.common.enums.command.Command;
import com.yanceysong.im.common.model.common.ClientInfo;
import com.yanceysong.im.common.model.user.UserSession;
import com.yanceysong.im.infrastructure.session.UserSessionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @ClassName MessageProducer
 * @Description 消息发送目标/数量选择 实体类
 * @date 2023/5/12 10:56
 * @Author yanceysong
 * @Version 1.0
 */
@Slf4j
@Component
public class MessageProducer extends AbstractMessageSend {

    @Resource
    private UserSessionService userSessionService;

    /**
     * 消息发送【兼容管理员和普通用户】
     *
     * @param receiverId       要发送消息接收人的id
     * @param command    指令
     * @param data       数据
     * @param appId      app的id
     * @param clientType 客户端类型
     * @param imei       客户端标识
     */
    public void sendMsgToUser(String receiverId, Command command, Object data, Integer appId, Integer clientType, String imei) {
        if (clientType != null && StringUtils.isNotBlank(imei)) {
            // (app 调用)普通用户发起的消息，发送给出本端以外的所有端
            ClientInfo clientInfo = new ClientInfo(appId, clientType, imei);
            sendToUserExceptClient(receiverId, command, data, clientInfo);
        } else {
            // (后台调用)管理员发起的消息(管理员没有 imei 号)，发送给所有端
            sendToUserAllClient(receiverId, command, data, appId);
        }
    }

    @Override
    public List<ClientInfo> sendToUserAllClient(String receiverId, Command command, Object data, Integer appId) {
        List<UserSession> userSessions = userSessionService.getUserSession(appId, receiverId);
        return userSessions.stream()
                // 筛出非空对象
                .filter(Objects::nonNull)
                // 消息发送
                .filter(session -> sendMessage(receiverId, command, data, session))
                .map(session -> new ClientInfo(session.getAppId(),
                        session.getClientType(), session.getImei()))
                .collect(Collectors.toList());
    }


    @Override
    public void sendToUserOneClient(String receiverId, Command command, Object data, ClientInfo clientInfo) {
        UserSession userSession = userSessionService.getUserSession(clientInfo.getAppId(), receiverId, clientInfo.getClientType(), clientInfo.getImei());
        sendMessage(receiverId, command, data, userSession);
    }

    @Override
    public void sendToUserExceptClient(String receiverId, Command command, Object data, ClientInfo clientInfo) {
        List<UserSession> userSession = userSessionService.getUserSession(clientInfo.getAppId(), receiverId);
        //成功发送的UserSession的集合
       userSession.stream()
                .filter(session -> !isMatch(session, clientInfo))
                .forEach(session -> sendMessage(receiverId, command, data, session));
    }

    /**
     * 匹配
     *
     * @param session    用户的信息
     * @param clientInfo 客户端的信息
     * @return 是否匹配
     */
    private boolean isMatch(UserSession session, ClientInfo clientInfo) {
        return Objects.equals(session.getAppId(), clientInfo.getAppId())
                && Objects.equals(session.getClientType(), clientInfo.getClientType())
                && Objects.equals(session.getImei(), clientInfo.getImei());
    }

}
