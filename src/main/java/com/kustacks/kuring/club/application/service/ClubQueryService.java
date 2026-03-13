package com.kustacks.kuring.club.application.service;

import com.kustacks.kuring.club.application.port.in.*;
import com.kustacks.kuring.club.application.port.in.dto.*;
import com.kustacks.kuring.club.application.port.out.*;
import com.kustacks.kuring.club.application.port.out.dto.*;
import com.kustacks.kuring.club.domain.*;
import com.kustacks.kuring.common.annotation.*;
import com.kustacks.kuring.common.exception.*;
import com.kustacks.kuring.storage.application.port.out.*;
import com.kustacks.kuring.user.application.port.out.*;
import com.kustacks.kuring.user.domain.*;
import lombok.*;
import org.springframework.transaction.annotation.*;

import java.time.*;
import java.util.*;
import java.util.stream.*;

import static com.kustacks.kuring.common.exception.code.ErrorCode.*;

@UseCase
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ClubQueryService implements ClubQueryUseCase {

    private final ClubQueryPort clubQueryPort;
    private final ClubSubscriptionQueryPort clubSubscriptionQueryPort;
    private final RootUserQueryPort rootUserQueryPort;
    private final StoragePort storagePort;

    @Override
    public List<ClubDivisionResult> getClubDivisions() {
        return Arrays.stream(ClubDivision.values())
                .map(ClubDivisionResult::from)
                .toList();
    }

    @Override
    public ClubListResult getClubs(ClubListCommand command) {
        RootUser rootUser = rootUserQueryPort.findRootUserByEmail(command.email()).orElse(null);

        ClubCategory category = command.category() != null
                ? ClubCategory.fromName(command.category())
                : null;

        List<ClubDivision> divisions = command.divisions() != null
                ? command.divisions().stream()
                .map(ClubDivision::fromName)
                .toList()
                : null;

        List<ClubReadModel> clubReadModels = clubQueryPort.searchClubs(
                category,
                divisions
        );

        List<Long> clubIds = clubReadModels.stream()
                .map(ClubReadModel::getId)
                .toList();

        Map<Long, Long> subscriberCountMap = clubSubscriptionQueryPort.countSubscribersByClubIds(clubIds);

        Map<Long, Boolean> subscribedMap = getSubscribedMap(clubIds, rootUser);

        List<ClubItemResult> clubItemResults =
                clubReadModels.stream()
                        .map(r -> convertClubItemResult(
                                r,
                                subscribedMap.getOrDefault(r.getId(), false),
                                subscriberCountMap.getOrDefault(r.getId(), 0L)
                        ))
                        .toList();

        return new ClubListResult(clubItemResults);
    }

    @Override
    public ClubDetailResult getClubDetail(ClubDetailCommand command) {
        Long clubId = command.clubId();
        String email = command.email();

        RootUser rootUser = rootUserQueryPort.findRootUserByEmail(email).orElse(null);

        ClubDetailReadModel clubDetailReadModel = clubQueryPort.findClubDetailById(clubId)
                .orElseThrow(() -> new NotFoundException(CLUB_NOT_FOUND));

        Long subscriberCount = clubSubscriptionQueryPort.countSubscribers(clubId);

        boolean isSubscribed = rootUser != null && clubSubscriptionQueryPort.existsSubscription(rootUser.getId(), clubId);

        ClubRecruitmentStatus recruitmentStatus =
                ClubRecruitmentStatus.from(
                        clubDetailReadModel.getRecruitStartAt(),
                        clubDetailReadModel.getRecruitEndAt(),
                        clubDetailReadModel.getIsAlways(),
                        LocalDateTime.now()
                );

        return convertClubDetailResult(
                clubDetailReadModel,
                subscriberCount,
                isSubscribed,
                recruitmentStatus
        );
    }

    private Map<Long, Boolean> getSubscribedMap(List<Long> clubIds, RootUser rootUser) {

        if (rootUser == null) {
            return Map.of();
        }

        Long rootUserId = rootUser.getId();

        List<Long> subscribedClubIds = clubSubscriptionQueryPort.findSubscribedClubIdsByRootUserIdAndClubIds(clubIds, rootUserId);

        return subscribedClubIds.stream()
                .collect(Collectors.toMap(id -> id, id -> true));
    }

    private ClubItemResult convertClubItemResult(
            ClubReadModel clubReadModel,
            boolean isSubscribed,
            Long subscriberCount
    ) {

        String iconImageUrl = null;
        String iconImagePath = clubReadModel.getIconImagePath();

        if (iconImagePath != null && !iconImagePath.isBlank()) {
            iconImageUrl = storagePort.getPresignedUrl(iconImagePath);
        }

        return new ClubItemResult(
                clubReadModel.getId(),
                clubReadModel.getName(),
                clubReadModel.getSummary(),
                iconImageUrl,
                clubReadModel.getCategory().getName(),
                clubReadModel.getDivision().getName(),
                isSubscribed,
                subscriberCount,
                clubReadModel.getRecruitStartDate(),
                clubReadModel.getRecruitEndDate()
        );
    }

    private ClubDetailResult convertClubDetailResult(
            ClubDetailReadModel clubDetailReadModel,
            Long subscriberCount,
            boolean isSubscribed,
            ClubRecruitmentStatus recruitmentStatus
    ) {
        ClubDetailResult.Location location = null;
        if (clubDetailReadModel.hasLocation()) {
            location = new ClubDetailResult.Location(
                    clubDetailReadModel.getBuilding(),
                    clubDetailReadModel.getRoom(),
                    clubDetailReadModel.getLon(),
                    clubDetailReadModel.getLat()
            );
        }

        String posterImageUrl = null;
        String posterImagePath = clubDetailReadModel.getPosterImagePath();

        if (posterImagePath != null && !posterImagePath.isBlank()) {
            posterImageUrl = storagePort.getPresignedUrl(posterImagePath);
        }

        return new ClubDetailResult(
                clubDetailReadModel.getId(),
                clubDetailReadModel.getName(),
                clubDetailReadModel.getSummary(),
                clubDetailReadModel.getCategory(),
                clubDetailReadModel.getDivision(),
                subscriberCount,
                isSubscribed,
                clubDetailReadModel.getInstagramUrl(),
                clubDetailReadModel.getYoutubeUrl(),
                clubDetailReadModel.getEtcUrl(),
                clubDetailReadModel.getDescription(),
                clubDetailReadModel.getQualifications(),
                recruitmentStatus,
                clubDetailReadModel.getRecruitStartAt(),
                clubDetailReadModel.getRecruitEndAt(),
                clubDetailReadModel.getApplyUrl(),
                posterImageUrl,
                location
        );
    }
}
