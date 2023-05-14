package com.kustacks.kuring.worker.scrap.client.staff;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

@Slf4j
@Component
public class ProxyJsoupClient implements JsoupClient {

    private final Queue<ProxyInfo> proxyQueue;

    public ProxyJsoupClient() {
        proxyQueue = new LinkedList<>();
    }

    @PostConstruct
    public void initProxyList() {
        proxyQueue.offer(new ProxyInfo("175.209.219.214", 8080));
        proxyQueue.offer(new ProxyInfo("58.120.171.37", 8080));
        proxyQueue.offer(new ProxyInfo("34.64.169.221", 80));
        proxyQueue.offer(new ProxyInfo("140.238.8.178", 8000));
        proxyQueue.offer(new ProxyInfo("15.164.154.142", 3128));
    }

    @Override
    public Document get(String url, int timeOut) throws IOException {
        MethodCallback methodCallback = (connection, ip, port) -> connection.proxy(ip, port).get();
        return proxyTemplate(url, timeOut, methodCallback);
    }

    @Override
    public Document post(String url, int timeOut, Map<String, String> requestBody) throws IOException {
        MethodCallback methodCallback = (connection, ip, port) -> {
            for (String key : requestBody.keySet()) {
                connection = connection.data(key, requestBody.get(key));
            }
            return connection.proxy(ip, port).post();
        };

        return proxyTemplate(url, timeOut, methodCallback);
    }

    private Document proxyTemplate(String url, int timeOut, MethodCallback callback) throws IOException {
        for (int idx = 0; idx < proxyQueue.size(); idx++) {
            ProxyInfo proxyInfo = proxyQueue.peek();
            log.info("[index:{}] proxy = {}:{}", idx, proxyInfo.getIp(), proxyInfo.getPort());

            try {
                return getDocument(url, timeOut, callback, proxyInfo);
            } catch (IOException e) {
                log.error("Jsoup 오류 발생. {}", e.getMessage());
                proxyQueue.poll();
            }
        }

        throw new IOException("사용 가능한 Proxy Ip가 없습니다.");
    }

    private Document getDocument(String url, int timeOut, MethodCallback callback, ProxyInfo proxyInfo) throws IOException {
        Connection connection = Jsoup.connect(url).timeout(timeOut);
        Document document = callback.sendRequest(connection, proxyInfo.getIp(), proxyInfo.getPort());
        log.info("{} 으로 성공!", proxyInfo.ip);
        return document;
    }

    @Getter
    private static class ProxyInfo {

        private final String ip;
        private final int port;

        public ProxyInfo(String ip, int port) {
            this.ip = ip;
            this.port = port;
        }
    }

    @FunctionalInterface
    private interface MethodCallback {
        Document sendRequest(Connection connection, String ip, int port) throws IOException;
    }
}
