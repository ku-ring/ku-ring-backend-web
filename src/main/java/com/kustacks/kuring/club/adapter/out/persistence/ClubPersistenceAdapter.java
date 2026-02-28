package com.kustacks.kuring.club.adapter.out.persistence;

import com.kustacks.kuring.club.application.port.out.ClubQueryPort;
import com.kustacks.kuring.club.application.port.out.ClubSubscriptionCommandPort;
import com.kustacks.kuring.club.application.port.out.ClubSubscriptionQueryPort;
import com.kustacks.kuring.club.application.port.out.dto.ClubDetailDto;
import com.kustacks.kuring.club.application.port.out.dto.ClubReadModel;
import com.kustacks.kuring.club.domain.Club;
import com.kustacks.kuring.club.domain.ClubSubscribe;
import com.kustacks.kuring.common.annotation.PersistenceAdapter;
import com.kustacks.kuring.user.domain.RootUser;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@PersistenceAdapter
@RequiredArgsConstructor
public class ClubPersistenceAdapter implements ClubQueryPort, ClubSubscriptionCommandPort, ClubSubscriptionQueryPort {

    private final ClubRepository clubRepository;
    private final ClubSubscribeRepository clubSubscribeRepository;

    @Override
    public List<ClubReadModel> searchClubs(
            String category,
            List<String> divisions
    ) {
        return clubRepository.searchClubs(category, divisions);
    }

    @Override
    public int countClubsByCategoryAndDivisions(String category, List<String> divisions) {
        return clubRepository.countClubsByCategoryAndDivisions(category, divisions);
    }

    @Override
    public Optional<ClubDetailDto> findClubDetailById(Long id) {
        return clubRepository.findClubDetailById(id);
    }

//    @Override
//    public boolean existsSubscription(Long clubId, Long loginUserId) {
//        return clubSubscribeRepository.existsByClubIdAndUser_LoginUserId(clubId, loginUserId);
//    }

    @Override
    public Optional<Club> findClubById(Long id) {
        return clubRepository.findById(id);
    }

    @Override
    public List<Long> findSubscribedClubIds(
            List<Long> clubIds,
            Long loginUserId
    ) {
        return clubSubscribeRepository
                .findByClubIdInAndUser_LoginUserId(clubIds, loginUserId)
                .stream()
                .map(sub -> sub.getClub().getId())
                .toList();
    }


    @Override
    public int countSubscribers(Long clubId) {
        return clubSubscribeRepository.countByClubId(clubId);
    }

    @Override
    public Map<Long, Integer> countSubscribersByClubIds(List<Long> clubIds) {

        if (clubIds == null || clubIds.isEmpty()) {
            return Map.of();
        }

        List<ClubSubscribe> subscriptions = clubSubscribeRepository.findByClubIdIn(clubIds);

        // groupby로 해도 될듯
        return subscriptions.stream()
                .collect(Collectors.groupingBy(
                        sub -> sub.getClub().getId(),
                        Collectors.collectingAndThen(
                                Collectors.counting(),
                                Long::intValue
                        )
                ));
    }

    @Override
    public List<Club> findClubsBetweenDates(LocalDateTime start, LocalDateTime end) {
        return clubRepository.findClubsBetweenDates(start, end);
    }

    @Override
    public List<Club> findNextDayRecruitEndClubs(LocalDateTime now) {
        LocalDate tomorrow = now.toLocalDate().plusDays(1);
        LocalDateTime startInclusive = tomorrow.atStartOfDay();
        LocalDateTime endExclusive = tomorrow.plusDays(1).atStartOfDay();
        return findClubsBetweenDates(startInclusive, endExclusive);
    }


    @Override
    public boolean existsSubscription(Long rootUserId, Long clubId) {
        return clubSubscribeRepository.existsByRootUserIdAndClubId(rootUserId, clubId);
    }

    @Override
    public void saveSubscription(RootUser rootUser, Club club) {
        clubSubscribeRepository.save(new ClubSubscribe(rootUser, club));
    }

    @Override
    public void deleteSubscription(RootUser rootUser, Club club) {
        clubSubscribeRepository.deleteByRootUserAndClub(rootUser, club);
    }

    @Override
    public long countSubscriptions(Long rootUserId) {
        return clubSubscribeRepository.countByRootUserId(rootUserId);
    }
}
