package com.kustacks.kuring.notice.adapter.out.persistence;

import com.kustacks.kuring.common.annotation.PersistenceAdapter;
import com.kustacks.kuring.notice.application.port.out.WhiteWordQueryPort;
import com.kustacks.kuring.notice.domain.WhiteWord;
import lombok.RequiredArgsConstructor;

import java.util.List;

@PersistenceAdapter
@RequiredArgsConstructor
public class WhitelistWordPersistenceAdapter implements WhiteWordQueryPort {

    private final WhitelistWordRepository whitelistWordRepository;

    @Override
    public List<WhiteWord> findAllByActive() {
        return whitelistWordRepository.findAllByIsActiveIsTrue();
    }
}
