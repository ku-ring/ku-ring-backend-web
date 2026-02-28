package com.kustacks.kuring.club.adapter.out.persistence;

import com.kustacks.kuring.club.application.port.out.dto.ClubDetailDto;
import com.kustacks.kuring.club.application.port.out.dto.ClubReadModel;
import com.kustacks.kuring.club.domain.Club;
import com.kustacks.kuring.club.domain.ClubCategory;
import com.kustacks.kuring.club.domain.ClubDivision;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ClubQueryRepository {

    List<ClubReadModel> searchClubs(ClubCategory category, List<ClubDivision> divisions);

    Optional<ClubDetailDto> findClubDetailById(Long id);

    List<Club> findClubsBetweenDates(LocalDateTime start, LocalDateTime end);
}
