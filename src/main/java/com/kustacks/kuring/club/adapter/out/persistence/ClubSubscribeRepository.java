package com.kustacks.kuring.club.adapter.out.persistence;

import com.kustacks.kuring.club.domain.ClubSubscribe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClubSubscribeRepository extends JpaRepository<ClubSubscribe, Long> {

    int countByClubId(Long clubId);

    boolean existsByClubIdAndUser_LoginUserId(Long clubId, Long loginUserId);
}