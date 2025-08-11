package com.kustacks.kuring.notice.application.port.out;

import com.kustacks.kuring.notice.domain.WhiteWord;

import java.util.List;

public interface WhiteWordQueryPort {
    List<WhiteWord> findAllByActive();
}
