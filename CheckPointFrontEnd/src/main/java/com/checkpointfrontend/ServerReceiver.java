package com.checkpointfrontend;

import java.lang.reflect.Type;
import java.util.function.Consumer;

import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

public class ServerReceiver {

    
    public static void connect(String username, Consumer<MessageFormat> onMessage) {
        try {
            StandardWebSocketClient client = new StandardWebSocketClient();
            WebSocketStompClient stompClient = new WebSocketStompClient(client);
            
            stompClient.setMessageConverter(new MappingJackson2MessageConverter());

            StompHeaders stompHeaders = new StompHeaders();
            stompHeaders.set("username", username);
            System.out.println("Connecting with headers: " + stompHeaders);

            stompClient.connectAsync(
                "ws://localhost:8080/ws",
                new WebSocketHttpHeaders(),
                stompHeaders,
                new StompSessionHandlerAdapter() {
                    @Override
                    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                        System.out.println("Connected as user: " + username);

                        session.subscribe("/user/queue/messages", new StompFrameHandler() {
                            @Override
                            public Type getPayloadType(StompHeaders headers) {
                                return MessageFormat.class; // Deserialize into MessageFormat
                            }

                            @Override
                            public void handleFrame(StompHeaders headers, Object payload) {
                                if (payload instanceof MessageFormat message) {
                                    onMessage.accept(message);
                                } else {
                                    System.err.println("Received unknown payload: " + payload);
                                }
                            }
                        });
                    }
                }
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}