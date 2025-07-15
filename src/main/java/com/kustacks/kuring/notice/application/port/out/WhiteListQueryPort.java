package com.kustacks.kuring.notice.application.port.out;

import com.kustacks.kuring.notice.domain.WhitelistWord;

import java.util.List;

public interface WhiteListQueryPort {
    List<WhitelistWord> findAllByActive();
}
