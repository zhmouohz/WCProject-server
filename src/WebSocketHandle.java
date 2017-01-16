/**
 * Created by HP on 2017/1/11.
 */

import java.io.IOException;
import java.util.ArrayList;
//import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/wc")
public class WebSocketHandle {
    private Session session;
    private static ArrayList<WebSocketHandle> webSocketSet = new ArrayList<WebSocketHandle>();
    public static ArrayList<String> waitList = new ArrayList<>();

    @OnOpen
    public void onOpen(Session session) {
        try {
            webSocketSet.add(this);
            this.session=session;
            String returnUserList = getReturnUserList("");
            System.out.println("onopen" + returnUserList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * WebSocket请求关闭
     */
    @OnClose
    public void onClose() {
        try {
            webSocketSet.remove(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnError
    public void onError(Throwable thr) {
        thr.printStackTrace();
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("来自客户端的消息:" + message);
        String returnUserList = getReturnUserList(message);
        System.out.println("getReturnUserList:" + returnUserList);
        for (WebSocketHandle user : webSocketSet) {
            try {
                user.session.getBasicRemote().sendText(returnUserList);
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
        }
    }

    private String getReturnUserList(String userName) {
        StringBuffer returnUserList = new StringBuffer();
        System.out.println(WebSocketHandle.waitList.size());
        boolean isOnList = false;
        for (int i = 0; i < WebSocketHandle.waitList.size(); i++) {
            if (userName != null && !userName.isEmpty() && WebSocketHandle.waitList.get(i).equals(userName)) {
                isOnList = true;
                WebSocketHandle.waitList.remove(i);
                i--;
            } else {
                returnUserList.append(WebSocketHandle.waitList.get(i)).append(",");
            }
        }
        if (!isOnList && userName != null && !userName.isEmpty()) {
            WebSocketHandle.waitList.add(userName);
            returnUserList.append(userName).append(",");
        }
        return returnUserList.toString().isEmpty()? "," : returnUserList.toString();
    }
}
