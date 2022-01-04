package com.kustacks.kuring.kuapi.api.staff;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ProxyJsoupClient implements JsoupClient {

    private final List<ProxyInfo> proxyList;

    public ProxyJsoupClient() {

        proxyList = new LinkedList<>();
        proxyList.add(new ProxyInfo("211.237.5.73", 	8899));
        proxyList.add(new ProxyInfo("175.125.23.246", 	8000));
        proxyList.add(new ProxyInfo("61.255.239.33", 	8008));
        proxyList.add(new ProxyInfo("222.231.47.230", 	3128));
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

        Document document = null;
        IOException jsoupException = null;
        int idx = 0;
        for (ProxyInfo proxyInfo : proxyList) {
            log.info("proxy = {}:{}", proxyInfo.getIp(), proxyInfo.getPort());
            log.info("idx = {}", idx);
            Connection connection = Jsoup.connect(url).timeout(timeOut);
            try {
                document = callback.sendRequest(connection, proxyInfo.getIp(), proxyInfo.getPort());
                log.info("{} 으로 성공!", proxyInfo.ip);
                break;
            } catch(IOException e) {
                log.error("Jsoup 오류 발생. {}", e.getMessage());
                jsoupException = e;
                ++idx;
            }
        }
        if(document != null) {
            if(idx > 0) {
                ProxyInfo bestProxy = proxyList.remove(idx);
                proxyList.add(0, bestProxy);
            }
            return document;
        } else {
            throw jsoupException;
        }
    }

    @Getter
    static class ProxyInfo {

        private final String ip;
        private final int port;

        public ProxyInfo(String ip, int port) {
            this.ip = ip;
            this.port = port;
        }
    }

    interface MethodCallback {
        Document sendRequest(Connection connection, String ip, int port) throws IOException;
    }
}
