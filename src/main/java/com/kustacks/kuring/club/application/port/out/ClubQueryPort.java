package com.kustacks.kuring.club.application.port.out;

import com.kustacks.kuring.club.application.port.out.dto.ClubDetailDto;
import com.kustacks.kuring.club.application.port.out.dto.ClubReadModel;
import com.kustacks.kuring.club.domain.Club;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ClubQueryPort {

    Optional<Club> findClubById(Long id);

    Optional<ClubDetailDto> findClubDetailById(Long id);

    List<ClubReadModel> searchClubs(String category, List<String> divisions);

    boolean existsSubscription(Long clubId, Long loginUserId);

    List<Long> findSubscribedClubIds(List<Long> clubIds, Long loginUserId);

    int countSubscribers(Long clubId);

    Map<Long, Integer> countSubscribersByClubIds(List<Long> clubIds);

    List<Club> findClubsBetweenDates(LocalDateTime start, LocalDateTime end);

    List<Club> findNextDayRecruitEndClubs(LocalDateTime now);
}
