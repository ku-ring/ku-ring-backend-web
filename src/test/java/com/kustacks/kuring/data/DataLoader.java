package com.kustacks.kuring.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class DataLoader {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    public DataLoader() {
    }

    /**
     * DB를 사용하는 테스트에서 초기화 데이터 삽입용 메서드
     * @author : zbqmgldjfh(https://github.com/zbqmgldjfh)
     */
    @Transactional
    public void loadData() {
        log.info("[call DataLoader]");

        log.info("[init complete DataLoader]");
    }
}
