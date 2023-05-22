package com.kustacks.kuring.worker.scrap.client;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

@Slf4j
@Component
public class ProxyJsoupClient implements JsoupClient {

    private static final String IP_PORT_DELIMITER = ":";
    private static final int IP_INDEX = 0;
    private static final int PORT_INDEX = 1;

    private final Queue<ProxyInfo> proxyQueue;

    @Value("${ip.proxy-list}")
    private List<String> proxyList;

    public ProxyJsoupClient() {
        proxyQueue = new LinkedList<>();
    }

    @PostConstruct
    public void initProxyList() {
        for(String proxyIp : proxyList) {
            String[] splitResults = proxyIp.split(IP_PORT_DELIMITER);
            proxyQueue.offer(new ProxyInfo(splitResults[IP_INDEX], Integer.parseInt(splitResults[PORT_INDEX])));
        }
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
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyInfo.getIp(), proxyInfo.getPort()));
        Connection connection = Jsoup.connect(url).proxy(proxy).timeout(timeOut);

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
