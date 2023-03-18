package com.kustacks.kuring.worker.api.staff;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class NormalJsoupClient implements JsoupClient {

    @Override
    public Document get(String url, int timeOut) throws IOException {
        return Jsoup.connect(url).timeout(timeOut).get();
    }

    @Override
    public Document post(String url, int timeOut, Map<String, String> requestBody) throws IOException {

        Connection connection = Jsoup.connect(url).timeout(timeOut);
        for (String key : requestBody.keySet()) {
            connection = connection.data(key, requestBody.get(key));
        }

        return connection.post();
    }
}
