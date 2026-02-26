package com.kustacks.kuring.club.adapter.out.persistence;

import com.kustacks.kuring.club.domain.Club;

import java.time.LocalDateTime;
import java.util.List;

public interface ClubQueryRepository {

    List<Club> findClubsBetweenDates(LocalDateTime start, LocalDateTime end);
}
