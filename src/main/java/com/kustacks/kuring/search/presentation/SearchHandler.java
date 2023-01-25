package com.kustacks.kuring.search.presentation;

import org.springframework.web.socket.WebSocketSession;

public interface SearchHandler {

    void handleTextMessage(WebSocketSession session, String keywords);
}
