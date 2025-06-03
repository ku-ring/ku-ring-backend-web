package com.kustacks.kuring.notice.application.port.out;

import com.kustacks.kuring.notice.domain.BadWord;

import java.util.List;

public interface BadWordsQueryPort {

    List<BadWord> findAllByActive();
}
