package com.kustacks.kuring.club.application.port.out;

import com.kustacks.kuring.club.domain.Club;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ClubQueryPort {

    Optional<Club> findClubById(Long id);

    List<Club> findClubsBetweenDates(LocalDateTime start, LocalDateTime end);

    List<Club> findNextDayRecruitEndClubs(LocalDateTime now);
}
