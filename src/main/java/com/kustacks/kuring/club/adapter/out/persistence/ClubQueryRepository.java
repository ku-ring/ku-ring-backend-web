package com.kustacks.kuring.club.adapter.out.persistence;

import com.kustacks.kuring.club.application.port.out.dto.*;
import com.kustacks.kuring.club.domain.*;

import java.time.*;
import java.util.*;

public interface ClubQueryRepository {

    List<ClubReadModel> searchClubs(ClubCategory category, List<ClubDivision> divisions);

    List<ClubReadModel> findClubReadModelsByIds(List<Long> ids);

    Optional<ClubDetailReadModel> findClubDetailById(Long id);

    List<Club> findClubsBetweenDates(LocalDateTime start, LocalDateTime end);
}
