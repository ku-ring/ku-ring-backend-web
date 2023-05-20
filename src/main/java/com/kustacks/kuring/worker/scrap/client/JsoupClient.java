package com.kustacks.kuring.worker.scrap.client;

import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Map;

public interface JsoupClient {
    Document get(String url, int timeOut) throws IOException;
    Document post(String url, int timeOut, Map<String, String> requestBody) throws IOException;
}
