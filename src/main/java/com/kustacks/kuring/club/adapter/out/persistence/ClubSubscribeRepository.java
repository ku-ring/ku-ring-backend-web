package com.kustacks.kuring.club.adapter.out.persistence;

import com.kustacks.kuring.club.domain.Club;
import com.kustacks.kuring.club.domain.ClubSubscribe;
import com.kustacks.kuring.user.domain.RootUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

interface ClubSubscribeRepository extends JpaRepository<ClubSubscribe, Long> {

    Long countByClubId(Long clubId);

    Long countByRootUserId(Long rootUserId);

    boolean existsByRootUserIdAndClubId(Long rootUserId, Long clubId);

    void deleteByRootUserAndClub(RootUser rootUser, Club club);

    @Query("""
               SELECT cs.club.id, COUNT(cs)
               FROM ClubSubscribe cs
               WHERE cs.club.id IN :clubIds
               GROUP BY cs.club.id
            """)
    List<Object[]> countSubscribersByClubIds(List<Long> clubIds);

    List<ClubSubscribe> findByClubIdInAndRootUserId(List<Long> clubIds, Long rootUserId);
}