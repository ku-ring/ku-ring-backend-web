package com.kustacks.kuring.notice.adapter.out.persistence;

import com.kustacks.kuring.common.annotation.PersistenceAdapter;
import com.kustacks.kuring.notice.application.port.out.WhiteListQueryPort;
import com.kustacks.kuring.notice.domain.WhitelistWord;
import lombok.RequiredArgsConstructor;

import java.util.List;

@PersistenceAdapter
@RequiredArgsConstructor
public class WhitelistWordPersistenceAdapter implements WhiteListQueryPort {

    private final WhitelistWordRepository whitelistWordRepository;

    @Override
    public List<WhitelistWord> findAllByActive() {
        return whitelistWordRepository.findAllByIsActiveIsTrue();
    }
}
