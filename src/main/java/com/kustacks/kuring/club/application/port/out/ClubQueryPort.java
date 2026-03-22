package com.kustacks.kuring.club.application.port.out;

import com.kustacks.kuring.club.application.port.out.dto.ClubDetailReadModel;
import com.kustacks.kuring.club.application.port.out.dto.ClubReadModel;
import com.kustacks.kuring.club.domain.Club;
import com.kustacks.kuring.club.domain.ClubCategory;
import com.kustacks.kuring.club.domain.ClubDivision;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ClubQueryPort {

    Optional<Club> findClubById(Long id);

    Optional<ClubDetailReadModel> findClubDetailById(Long id);

    List<ClubReadModel> findClubReadModelsByIds(List<Long> ids);

    List<ClubReadModel> searchClubs(ClubCategory category, List<ClubDivision> divisions);

    List<Club> findClubsBetweenDates(LocalDateTime start, LocalDateTime end);

    List<Club> findNextDayRecruitEndClubs(LocalDateTime now);
}
