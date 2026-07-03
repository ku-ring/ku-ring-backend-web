package com.kustacks.kuring.club.adapter.out.persistence;

import com.kustacks.kuring.club.application.port.out.ClubCommandPort;
import com.kustacks.kuring.club.application.port.out.ClubQueryPort;
import com.kustacks.kuring.club.application.port.out.ClubSubscriptionCommandPort;
import com.kustacks.kuring.club.application.port.out.ClubSubscriptionQueryPort;
import com.kustacks.kuring.club.application.port.out.dto.ClubDetailReadModel;
import com.kustacks.kuring.club.application.port.out.dto.ClubReadModel;
import com.kustacks.kuring.club.domain.Club;
import com.kustacks.kuring.club.domain.ClubCategory;
import com.kustacks.kuring.club.domain.ClubDivision;
import com.kustacks.kuring.club.domain.ClubSns;
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
public class ClubPersistenceAdapter implements ClubQueryPort, ClubCommandPort,
        ClubSubscriptionCommandPort, ClubSubscriptionQueryPort{

    private final ClubRepository clubRepository;
    private final ClubSnsRepository clubSnsRepository;
    private final ClubSubscribeRepository clubSubscribeRepository;

    @Override
    public List<ClubReadModel> searchClubs(
            ClubCategory category,
            List<ClubDivision> divisions
    ) {
        return clubRepository.searchClubs(category, divisions);
    }

    @Override
    public Optional<ClubDetailReadModel> findClubDetailById(Long id) {
        return clubRepository.findClubDetailById(id);
    }

    @Override
    public List<ClubReadModel> findClubReadModelsByIds(List<Long> ids) {
        return clubRepository.findClubReadModelsByIds(ids);
    }

    @Override
    public Optional<Club> findClubById(Long id) {
        return clubRepository.findById(id);
    }

    @Override
    public List<Long> findSubscribedClubIdsByRootUserIdAndClubIds(
            List<Long> clubIds,
            Long rootUserId
    ) {
        if (clubIds == null || clubIds.isEmpty()) {
            return List.of();
        }

        return clubSubscribeRepository.findByClubIdInAndRootUserId(clubIds, rootUserId);
    }

    @Override
    public List<Long> findSubscribedClubIdsByRootUserId(Long rootUserId) {
        return clubSubscribeRepository.findClubIdsByRootUserId(rootUserId);
    }

    @Override
    public Long countSubscribers(Long clubId) {
        return clubSubscribeRepository.countByClubId(clubId);
    }

    @Override
    public Map<Long, Long> countSubscribersByClubIds(List<Long> clubIds) {

        if (clubIds == null || clubIds.isEmpty()) {
            return Map.of();
        }

        List<ClubSubscribeRepository.ClubSubscriberCountProjection> subscriptions = clubSubscribeRepository.countSubscribersByClubIds(clubIds);

        return subscriptions.stream()
                .collect(Collectors.toMap(
                        ClubSubscribeRepository.ClubSubscriberCountProjection::getClubId,
                        ClubSubscribeRepository.ClubSubscriberCountProjection::getSubscriberCount

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
    public Long countSubscriptions(Long rootUserId) {
        return clubSubscribeRepository.countByRootUserId(rootUserId);
    }

    @Override
    public boolean existsByNameAndDivision(String name, ClubDivision division) {
        return clubRepository.existsByNameAndDivision(name, division);
    }

    @Override
    public Club save(Club club) {
        return clubRepository.save(club);
    }

    @Override
    public void saveAll(List<ClubSns> toSave) {
        clubSnsRepository.saveAll(toSave);
    }
}
