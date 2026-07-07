package com.kustacks.kuring.club.adapter.out.persistence;

import com.kustacks.kuring.club.domain.Club;
import org.springframework.data.jpa.repository.JpaRepository;

interface ClubRepository extends JpaRepository<Club, Long>, ClubQueryRepository {
}
