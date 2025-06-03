package com.kustacks.kuring.notice.adapter.out.persistence;

import com.kustacks.kuring.common.annotation.PersistenceAdapter;
import com.kustacks.kuring.notice.application.port.out.BadWordsQueryPort;
import com.kustacks.kuring.notice.domain.BadWord;
import lombok.RequiredArgsConstructor;

import java.util.List;

@PersistenceAdapter
@RequiredArgsConstructor
public class BadWordPersistenceAdapter implements BadWordsQueryPort {

    private final BadWordRepository badWordRepository;

    @Override
    public List<BadWord> findAllByActive() {
        return badWordRepository.findAllByIsActiveIsTrue();
    }
}
