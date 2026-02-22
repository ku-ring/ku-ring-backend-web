package com.kustacks.kuring.club.adapter.out.persistence;

import com.kustacks.kuring.club.domain.ClubSubscribe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClubSubscribeRepository extends JpaRepository<ClubSubscribe, Long> {

    int countByClubId(Long clubId);

    boolean existsByClubIdAndUser_LoginUserId(Long clubId, Long loginUserId);

    List<ClubSubscribe> findByClubIdIn(List<Long> clubIds);

    List<ClubSubscribe> findByClubIdInAndUser_LoginUserId(List<Long> clubIds, Long loginUserId);
}