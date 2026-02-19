package com.kustacks.kuring.club.adapter.out.persistence;

import com.kustacks.kuring.club.domain.Club;
import com.kustacks.kuring.club.domain.ClubSubscribe;
import com.kustacks.kuring.user.domain.RootUser;
import org.springframework.data.jpa.repository.JpaRepository;

interface ClubSubscribeRepository extends JpaRepository<ClubSubscribe, Long> {

    boolean existsByRootUserIdAndClubId(Long rootUserId, Long clubId);

    long countByRootUserId(Long rootUserId);

    void deleteByRootUserAndClub(RootUser rootUser, Club club);
}
