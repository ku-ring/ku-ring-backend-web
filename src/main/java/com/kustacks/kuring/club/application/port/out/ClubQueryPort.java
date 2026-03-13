package com.kustacks.kuring.club.application.port.out;

import com.kustacks.kuring.club.application.port.out.dto.*;
import com.kustacks.kuring.club.domain.*;

import java.time.*;
import java.util.*;

public interface ClubQueryPort {

    Optional<Club> findClubById(Long id);

    Optional<ClubDetailReadModel> findClubDetailById(Long id);

    List<ClubReadModel> findClubReadModelsByIds(List<Long> ids);

    List<ClubReadModel> searchClubs(ClubCategory category, List<ClubDivision> divisions);

    List<Club> findClubsBetweenDates(LocalDateTime start, LocalDateTime end);

    List<Club> findNextDayRecruitEndClubs(LocalDateTime now);
}
