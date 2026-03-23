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

    interface ClubSubscriberCountProjection {
        Long getClubId();

        Long getSubscriberCount();
    }

    @Query("""
               SELECT cs.club.id AS clubId, COUNT(cs) AS subscriberCount
               FROM ClubSubscribe cs
               WHERE cs.club.id IN :clubIds
               GROUP BY cs.club.id
            """)
    List<ClubSubscriberCountProjection> countSubscribersByClubIds(List<Long> clubIds);

    @Query("""
            select cs.club.id
            from ClubSubscribe cs
            where cs.club.id in :clubIds
              and cs.rootUser.id = :rootUserId
            """)
    List<Long> findByClubIdInAndRootUserId(List<Long> clubIds, Long rootUserId);

    @Query("""
            select cs.club.id 
            from ClubSubscribe cs 
            where cs.rootUser.id = :rootUserId
            """)
    List<Long> findClubIdsByRootUserId(Long rootUserId);
}
