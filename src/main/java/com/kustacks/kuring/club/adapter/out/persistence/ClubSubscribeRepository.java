package com.kustacks.kuring.club.adapter.out.persistence;

import com.kustacks.kuring.club.domain.Club;
import com.kustacks.kuring.club.domain.ClubSubscribe;
import com.kustacks.kuring.user.domain.RootUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface ClubSubscribeRepository extends JpaRepository<ClubSubscribe, Long> {

    int countByClubId(Long clubId);

    long countByRootUserId(Long rootUserId);

    boolean existsByRootUserIdAndClubId(Long rootUserId, Long clubId);

    void deleteByRootUserAndClub(RootUser rootUser, Club club);

    boolean existsByClubIdAndUser_LoginUserId(Long clubId, Long loginUserId);

    List<ClubSubscribe> findByClubIdIn(List<Long> clubIds);

    List<ClubSubscribe> findByClubIdInAndUser_LoginUserId(List<Long> clubIds, Long loginUserId);
}