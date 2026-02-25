package com.kustacks.kuring.club.application.port.out;

import com.kustacks.kuring.club.application.port.out.dto.ClubDetailDto;
import com.kustacks.kuring.club.application.port.out.dto.ClubReadModel;
import com.kustacks.kuring.common.data.Cursor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ClubQueryPort {

    List<ClubReadModel> searchClubs(String category, List<String> divisions, Cursor cursor, int size, String sortBy, LocalDateTime now);

    int countClubsByCategoryAndDivisions(String category, List<String> divisions);

    Optional<ClubDetailDto> findClubDetailById(Long id);

    boolean existsSubscription(Long clubId, Long loginUserId);

    List<Long> findSubscribedClubIds(List<Long> clubIds, Long loginUserId);

    int countSubscribers(Long clubId);

    Map<Long, Integer> countSubscribersByClubIds(List<Long> clubIds);
}
