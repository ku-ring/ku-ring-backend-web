package com.kustacks.kuring.notice.adapter.out.persistence;

import com.kustacks.kuring.notice.domain.BadWord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BadWordRepository extends JpaRepository<BadWord, Long> {

    List<BadWord> findAllByIsActiveIsTrue();
}
