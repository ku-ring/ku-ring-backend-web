package com.kustacks.kuring.notice.adapter.out.persistence;

import com.kustacks.kuring.notice.domain.WhitelistWord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WhitelistWordRepository extends JpaRepository<WhitelistWord, Long> {
    List<WhitelistWord> findAllByIsActiveIsTrue();
}
