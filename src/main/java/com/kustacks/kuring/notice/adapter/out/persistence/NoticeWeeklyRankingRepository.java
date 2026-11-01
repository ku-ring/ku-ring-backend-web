package com.kustacks.kuring.notice.adapter.out.persistence;

import com.kustacks.kuring.notice.domain.NoticeWeeklyRanking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

interface NoticeWeeklyRankingRepository extends JpaRepository<NoticeWeeklyRanking, Long> {

    Optional<NoticeWeeklyRanking> findTopByOrderByWeekStartDateDesc();

    List<NoticeWeeklyRanking> findAllByWeekStartDateOrderByRankingAsc(LocalDate weekStartDate);

    @Modifying
    @Query("DELETE FROM NoticeWeeklyRanking r WHERE r.weekStartDate = :weekStartDate")
    void deleteAllByWeekStartDate(@Param("weekStartDate") LocalDate weekStartDate);
}
