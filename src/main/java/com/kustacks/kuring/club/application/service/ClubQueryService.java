package com.kustacks.kuring.club.application.service;

import com.kustacks.kuring.club.application.port.in.ClubQueryUseCase;
import com.kustacks.kuring.club.application.port.in.dto.ClubDetailCommand;
import com.kustacks.kuring.club.application.port.in.dto.ClubDetailResult;
import com.kustacks.kuring.club.application.port.in.dto.ClubDivisionResult;
import com.kustacks.kuring.club.application.port.in.dto.ClubItemResult;
import com.kustacks.kuring.club.application.port.in.dto.ClubListCommand;
import com.kustacks.kuring.club.application.port.in.dto.ClubListResult;
import com.kustacks.kuring.club.application.port.out.ClubQueryPort;
import com.kustacks.kuring.club.application.port.out.ClubSubscriptionQueryPort;
import com.kustacks.kuring.club.application.port.out.dto.ClubDetailDto;
import com.kustacks.kuring.club.application.port.out.dto.ClubReadModel;
import com.kustacks.kuring.club.domain.ClubCategory;
import com.kustacks.kuring.club.domain.ClubDivision;
import com.kustacks.kuring.club.domain.ClubRecruitmentStatus;
import com.kustacks.kuring.common.annotation.UseCase;
import com.kustacks.kuring.common.exception.NotFoundException;
import com.kustacks.kuring.user.application.port.out.RootUserQueryPort;
import com.kustacks.kuring.user.domain.RootUser;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.kustacks.kuring.common.exception.code.ErrorCode.CLUB_NOT_FOUND;

@UseCase
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ClubQueryService implements ClubQueryUseCase {

    private final ClubQueryPort clubQueryPort;
    private final ClubSubscriptionQueryPort clubSubscriptionQueryPort;
    private final RootUserQueryPort rootUserQueryPort;

    @Override
    public List<ClubDivisionResult> getClubDivisions() {
        return Arrays.stream(ClubDivision.values())
                .map(ClubDivisionResult::from)
                .toList();
    }

    @Override
    public ClubListResult getClubs(ClubListCommand command) {
        Optional<RootUser> rootUser = rootUserQueryPort.findRootUserByEmail(command.email());

        ClubCategory category = command.category() != null
                ? ClubCategory.fromName(command.category())
                : null;

        List<ClubDivision> divisions = command.divisionList() != null
                ? command.divisionList().stream()
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

        Optional<RootUser> rootUser = rootUserQueryPort.findRootUserByEmail(email);

        ClubDetailDto clubDetailDto = clubQueryPort.findClubDetailById(clubId)
                .orElseThrow(() -> new NotFoundException(CLUB_NOT_FOUND));

        Long subscriberCount = clubSubscriptionQueryPort.countSubscribers(clubId);

        boolean isSubscribed = false;
        if (rootUser.isPresent()) {
            Long rootUserId = rootUser.get().getId();
            isSubscribed = clubSubscriptionQueryPort.existsSubscription(rootUserId, clubId);
        }

        ClubRecruitmentStatus recruitmentStatus =
                ClubRecruitmentStatus.from(
                        clubDetailDto.getRecruitStartAt(),
                        clubDetailDto.getRecruitEndAt(),
                        clubDetailDto.getIsAlways(),
                        LocalDateTime.now()
                );

        return convertClubDetailResult(
                clubDetailDto,
                subscriberCount,
                isSubscribed,
                recruitmentStatus
        );
    }

    private Map<Long, Boolean> getSubscribedMap(List<Long> clubIds, Optional<RootUser> rootUser) {

        if (rootUser.isEmpty()) {
            return Map.of();
        }

        Long rootUserId = rootUser.get().getId();

        List<Long> subscribedClubIds = clubSubscriptionQueryPort.findSubscribedClubIds(clubIds, rootUserId);

        return subscribedClubIds.stream()
                .collect(Collectors.toMap(id -> id, id -> true));
    }

    private ClubItemResult convertClubItemResult(
            ClubReadModel clubReadModel,
            boolean isSubscribed,
            Long subscriberCount
    ) {
        return new ClubItemResult(
                clubReadModel.getId(),
                clubReadModel.getName(),
                clubReadModel.getSummary(),
                clubReadModel.getIconImageUrl(),
                clubReadModel.getCategory().getName(),
                clubReadModel.getDivision().getName(),
                isSubscribed,
                subscriberCount,
                clubReadModel.getRecruitStartDate(),
                clubReadModel.getRecruitEndDate()
        );
    }

    private ClubDetailResult convertClubDetailResult(
            ClubDetailDto clubDetailDto,
            Long subscriberCount,
            boolean isSubscribed,
            ClubRecruitmentStatus recruitmentStatus
    ) {
        ClubDetailResult.Location location = null;
        if (clubDetailDto.hasLocation()) {
            location = new ClubDetailResult.Location(
                    clubDetailDto.getBuilding(),
                    clubDetailDto.getRoom(),
                    clubDetailDto.getLon(),
                    clubDetailDto.getLat()
            );
        }

        return new ClubDetailResult(
                clubDetailDto.getId(),
                clubDetailDto.getName(),
                clubDetailDto.getSummary(),
                clubDetailDto.getCategory(),
                clubDetailDto.getDivision(),
                subscriberCount,
                isSubscribed,
                clubDetailDto.getInstagramUrl(),
                clubDetailDto.getYoutubeUrl(),
                clubDetailDto.getEtcUrl(),
                clubDetailDto.getDescription(),
                clubDetailDto.getQualifications(),
                recruitmentStatus,
                clubDetailDto.getRecruitStartAt(),
                clubDetailDto.getRecruitEndAt(),
                clubDetailDto.getApplyUrl(),
                clubDetailDto.getPosterImagePath(),
                location
        );
    }
}
