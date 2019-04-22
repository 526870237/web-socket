package com.ribeen.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * WebSocket服务
 *
 * @author paulandcode paulandcode@gmail.com
 * @since 11/22/2018 8:53 PM
 */
@Slf4j
@ServerEndpoint(value = "/web-socket/{clientGroupId}")
@Component
public class WebSocket {
    /**
     * 静态变量, 用来记录当前在线连接数, 应该把它设计成线程安全的.
     */
    private static int onlineCount = 0;
    /**
     * concurrent包的线程安全Set, 用来存放每个客户端对应的MyWebSocket对象.
     */
    private static CopyOnWriteArraySet<WebSocket> webSocketSet = new CopyOnWriteArraySet<>();
    /**
     * 与某个客户端的连接会话, 需要通过它来给客户端发送数据.
     */
    private Session session;
    /**
     * 连接的客户端组ID.
     */
    private String clientGroupId;

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam(value = "clientGroupId") String clientGroupId) {
        this.clientGroupId = clientGroupId;
        this.session = session;
        webSocketSet.add(this);
        synchronized (this) {
            log.info("有新连接加入! 客户端组ID为: " + clientGroupId + ". 当前在线人数为" + ++onlineCount);
        }
        try {
            sendMessage("连接成功");
        } catch (IOException e) {
            log.error("web-socket IO异常");
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        // 从set中删除
        webSocketSet.remove(this);
        synchronized (this) {
            log.info("有一连接关闭！当前在线人数为" + --onlineCount);
        }
    }

    /**
     * 收到客户端消息后转发消息
     *
     * @param message 客户端发送过来的消息, 格式要求为: {"msg":"测试","groupId":1}
     * @param session 客户端session
     */
    @OnMessage(maxMessageSize = 999999L)
    public void onMessage(String message, Session session) {
        log.info("来自客户端(sessionId为: " + session.getId() + ")的消息: " + message);
        JSONObject jsonObject = null;
        try {
            jsonObject = JSON.parseObject(message);
        } catch (Exception e) {
            log.error("消息格式不正确, 无法转发消息! ");
        }
        if (jsonObject != null) {
            Object msg = jsonObject.get("msg");
            Object groupId = jsonObject.get("groupId");
            if (groupId != null && msg != null) {
                // 进行消息转发
                Map<String, Set<WebSocket>> webSocketSets = formatWebSocketSet();
                Set<WebSocket> webSockets = webSocketSets.get(groupId.toString());
                if (webSockets != null) {
                    for (WebSocket webSocket : webSockets) {
                        try {
                            webSocket.sendMessage(msg.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                log.error("消息格式不正确, 无法转发消息! ");
            }
        }
    }

    /**
     * web-socket发生错误
     *
     * @param session 某个session
     * @param error   错误
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("sessionId为: " + session.getId() + "的session发生错误: " + error);
    }

    /**
     * 向客户端发送消息
     *
     * @param message 消息
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    private String getClientGroupId() {
        return clientGroupId;
    }

    /**
     * 将Set中WebSocket整理到Map中, 客户端组ID相同的都放到一个key里
     *
     * @return java.util.Map<java.lang.String   ,   java.util.Set   <   com.ribeen.utils.WebSocket>>
     */
    public static Map<String, Set<WebSocket>> formatWebSocketSet() {
        Map<String, Set<WebSocket>> webSocketSets = new HashMap<>(webSocketSet.size());
        for (WebSocket webSocket : webSocketSet) {
            String clientGroupId = webSocket.getClientGroupId();
            Set<WebSocket> webSockets = webSocketSets.get(clientGroupId);
            if (webSockets == null) {
                webSockets = new HashSet<>();
            }
            webSockets.add(webSocket);
            webSocketSets.put(clientGroupId, webSockets);
        }
        return webSocketSets;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WebSocket webSocket = (WebSocket) o;
        return Objects.equals(session, webSocket.session) &&
                Objects.equals(clientGroupId, webSocket.clientGroupId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(session, clientGroupId);
    }
}
