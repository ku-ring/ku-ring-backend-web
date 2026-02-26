package com.kustacks.kuring.club.adapter.out.persistence;

import com.kustacks.kuring.club.application.port.out.ClubQueryPort;
import com.kustacks.kuring.club.application.port.out.ClubSubscriptionCommandPort;
import com.kustacks.kuring.club.application.port.out.ClubSubscriptionQueryPort;
import com.kustacks.kuring.club.domain.Club;
import com.kustacks.kuring.club.domain.ClubSubscribe;
import com.kustacks.kuring.common.annotation.PersistenceAdapter;
import com.kustacks.kuring.user.domain.RootUser;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@PersistenceAdapter
@RequiredArgsConstructor
public class ClubPersistenceAdapter implements ClubQueryPort, ClubSubscriptionCommandPort, ClubSubscriptionQueryPort {

    private final ClubRepository clubRepository;
    private final ClubSubscribeRepository clubSubscribeRepository;

    @Override
    public Optional<Club> findClubById(Long id) {
        return clubRepository.findById(id);
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
