package com.kustacks.kuring.controller.handler;

import org.springframework.web.socket.WebSocketSession;

public interface SearchHandler {

    void handleTextMessage(WebSocketSession session, String keywords);
}
