package ohih.town.webSocket;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import ohih.town.constants.DomainConst;
import ohih.town.constants.UtilityConst;
import ohih.town.utilities.Utilities;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class ChatHandler extends TextWebSocketHandler {
    private static Map<WebSocketSession, String> map = new HashMap<>();
    private String userName = "";

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        JsonArray userList = new JsonArray();
        userName = Utilities.createCode(5);

        map.put(session, userName);

        for (String value : map.values()) {
            userList.add(value);
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(DomainConst.USERNAME, userName);
        jsonObject.addProperty(DomainConst.USER_COUNT, map.size());
        jsonObject.add(DomainConst.USER_LIST, userList);
        jsonObject.addProperty(UtilityConst.MESSAGE, "\"" + map.get(session) + "\"" + " has joined chat.");


        for (WebSocketSession webSocketSession : map.keySet()) {
            webSocketSession.sendMessage(new TextMessage(jsonObject.toString()));
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        JsonArray userList = new JsonArray();

        String sender = map.get(session);
        String payload = message.getPayload();

        for (String value : map.values()) {
            userList.add(value);
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(DomainConst.USERNAME, userName);
        jsonObject.addProperty(DomainConst.USER_COUNT, map.size());
        jsonObject.add(DomainConst.USER_LIST, userList);
        jsonObject.addProperty(UtilityConst.MESSAGE, sender + ": " + payload);


        for (WebSocketSession webSocketSession : map.keySet()) {
            webSocketSession.sendMessage(new TextMessage(jsonObject.toString()));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String left = map.get(session);

        map.remove(session);

        JsonArray userList = new JsonArray();

        for (String value : map.values()) {
            userList.add(value);
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(DomainConst.USERNAME, userName);
        jsonObject.addProperty(DomainConst.USER_COUNT, map.size());
        jsonObject.add(DomainConst.USER_LIST, userList);
        jsonObject.addProperty(UtilityConst.MESSAGE, "\"" + left + "\"" + " has left chat.");


        for (WebSocketSession webSocketSession : map.keySet()) {
            webSocketSession.sendMessage(new TextMessage(jsonObject.toString()));
        }
    }
}
