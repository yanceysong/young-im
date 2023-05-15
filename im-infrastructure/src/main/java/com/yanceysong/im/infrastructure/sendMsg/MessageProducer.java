package com.yanceysong.im.infrastructure.sendMsg;

import com.yanceysong.im.common.enums.command.Command;
import com.yanceysong.im.common.model.ClientInfo;
import com.yanceysong.im.common.model.UserSession;
import com.yanceysong.im.infrastructure.session.UserSessionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @ClassName MessageProducer
 * @Description
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
     * @param toId       要发送消息接收人的id
     * @param command    指令
     * @param data       数据
     * @param appId      app的id
     * @param clientType 客户端类型
     * @param imei       客户端标识
     */
    public void sendMsgToUser(String toId, Command command, Object data, Integer appId, Integer clientType, String imei) {
        if (clientType != null && StringUtils.isNotBlank(imei)) {
            // (app 调用)普通用户发起的消息，发送给出本端以外的所有端
            ClientInfo clientInfo = new ClientInfo(appId, clientType, imei);
            sendToUserExceptClient(toId, command, data, clientInfo);
        } else {
            // (后台调用)管理员发起的消息(管理员没有 imei 号)，发送给所有端
            sendToUserAllClient(toId, command, data, appId);
        }
    }

    @Override
    public List<ClientInfo> sendToUserAllClient(String toId, Command command, Object data, Integer appId) {
        List<UserSession> userSession = userSessionService.getUserSession(appId, toId);
        return userSession.stream()
                // 筛出非空对象
                .filter(Objects::nonNull)
                // 消息发送
                .filter(session -> sendMessage(toId, command, data, session))
                .map(session -> new ClientInfo(session.getAppId(),
                        session.getClientType(), session.getImei()))
                .collect(Collectors.toList());
    }


    @Override
    public void sendToUserOneClient(String toId, Command command, Object data, ClientInfo clientInfo) {
        UserSession userSession = userSessionService.getUserSession(clientInfo.getAppId(), toId, clientInfo.getClientType(), clientInfo.getImei());
        sendMessage(toId, command, data, userSession);
    }

    @Override
    public void sendToUserExceptClient(String toId, Command command, Object data, ClientInfo clientInfo) {
        List<UserSession> userSession = userSessionService.getUserSession(clientInfo.getAppId(), toId);
        //成功发送的UserSession的集合
        List<UserSession> sendSuccessUserSessionList = userSession.stream()
                .filter(session -> !isMatch(session, clientInfo))
                .filter(session -> sendMessage(toId, command, data, session)).collect(Collectors.toList());
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