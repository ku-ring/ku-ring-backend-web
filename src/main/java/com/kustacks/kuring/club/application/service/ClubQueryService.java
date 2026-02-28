package com.kustacks.kuring.club.application.service;

import com.kustacks.kuring.club.application.port.in.ClubQueryUseCase;
import com.kustacks.kuring.club.application.port.in.dto.ClubDetailCommand;
import com.kustacks.kuring.club.application.port.in.dto.ClubDetailResult;
import com.kustacks.kuring.club.application.port.in.dto.ClubDivisionResult;
import com.kustacks.kuring.club.application.port.in.dto.ClubItemResult;
import com.kustacks.kuring.club.application.port.in.dto.ClubListCommand;
import com.kustacks.kuring.club.application.port.in.dto.ClubListResult;
import com.kustacks.kuring.club.application.port.out.ClubQueryPort;
import com.kustacks.kuring.club.application.port.out.dto.ClubDetailDto;
import com.kustacks.kuring.club.application.port.out.dto.ClubReadModel;
import com.kustacks.kuring.club.domain.ClubDivision;
import com.kustacks.kuring.common.annotation.UseCase;
import com.kustacks.kuring.common.exception.NotFoundException;
import com.kustacks.kuring.user.application.port.out.RootUserQueryPort;
import com.kustacks.kuring.user.domain.RootUser;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

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

        List<ClubReadModel> clubReadModels = clubQueryPort.searchClubs(
                command.category(),
                command.divisionList()
        );

        List<Long> clubIds = clubReadModels.stream()
                .map(ClubReadModel::getId)
                .toList();

        Map<Long, Integer> subscriberCountMap = clubQueryPort.countSubscribersByClubIds(clubIds);

        // 구독 관련 메서드화 하면 좋을듯!
        List<Long> subscribedClubIds = List.of();
        if (rootUser.isPresent()) {
            Long rootUserId = rootUser.get().getId();
            subscribedClubIds = clubQueryPort.findSubscribedClubIds(clubIds, rootUserId);
        }

        Map<Long, Boolean> subscribedMap = subscribedClubIds.stream()
                .collect(Collectors.toMap(id -> id, id -> true));

        List<ClubItemResult> clubItemResults =
                clubReadModels.stream()
                        .map(r -> new ClubItemResult(
                                r.getId(),
                                r.getName(),
                                r.getSummary(),
                                r.getIconImageUrl(),
                                r.getCategory().getName(),
                                r.getDivision().getName(),
                                subscribedMap.getOrDefault(r.getId(), false),
                                subscriberCountMap.getOrDefault(r.getId(), 0),
                                r.getRecruitStartDate(),
                                r.getRecruitEndDate()
                        ))
                        .toList();

        return new ClubListResult(clubItemResults);
    }

    @Override
    public ClubDetailResult getClubDetail(ClubDetailCommand command) {
        Long clubId = command.clubId();
        String email = command.email();

        Optional<RootUser> rootUser = rootUserQueryPort.findRootUserByEmail(email);

        // dto -> readmodel로 이름 수정
        ClubDetailDto dto = clubQueryPort.findClubDetailById(clubId)
                .orElseThrow(() -> new NotFoundException(CLUB_NOT_FOUND));

        int subscriberCount = clubQueryPort.countSubscribers(clubId);

        boolean isSubscribed = false;
        if (rootUser.isPresent()) {
            Long rootUserId = rootUser.get().getId();
            isSubscribed = clubQueryPort.existsSubscription(rootUserId, clubId);
        }

        ClubDetailResult.Location location = dto.hasLocation() ?
                new ClubDetailResult.Location(
                        dto.getBuilding(),
                        dto.getRoom(),
                        dto.getLon(),
                        dto.getLat()
                )
                : null;

        return new ClubDetailResult(
                dto.getId(),
                dto.getName(),
                dto.getSummary(),
                dto.getCategory(),
                dto.getDivision(),
                subscriberCount,
                isSubscribed,
                dto.getInstagramUrl(),
                dto.getYoutubeUrl(),
                dto.getEtcUrl(),
                dto.getDescription(),
                dto.getQualifications(),
                dto.getRecruitmentStatus(),
                dto.getRecruitStartAt(),
                dto.getRecruitEndAt(),
                dto.getApplyUrl(),
                dto.getPosterImagePath(),
                location
        );
    }

}
