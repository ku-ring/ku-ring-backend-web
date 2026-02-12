package com.kustacks.kuring.storage.adapter.out;

import com.kustacks.kuring.storage.application.port.out.StoragePort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.*;

@Profile("local | test")
@Slf4j
@Service
public class MockStorageAdapter implements StoragePort {

    private static final String MOCK_SERVER = "https://mock.ku-ring.com/";

    @Override
    public void upload(InputStream inputStream, String key, String contentType) {
        log.info(String.format("%s 파일 저장 완료", key));
    }

    @Override
    public URL getPresignedUrl(String key) {
        try {
            return new URL(MOCK_SERVER + key);
        } catch (MalformedURLException e) {
            log.error("잘못된 URL입니다.");
            return null;
        }
    }

    @Override
    public void delete(String key) {
        log.info(String.format("%s 파일 제거 완료.", key));
    }
}
