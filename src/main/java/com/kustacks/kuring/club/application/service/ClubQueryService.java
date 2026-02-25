package com.kustacks.kuring.club.application.service;

import com.kustacks.kuring.club.application.port.in.ClubQueryUseCase;
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
import com.kustacks.kuring.common.data.CursorBasedList;
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
    private final RootUserQueryPort rootUserQueryPort;

    @Override
    public List<ClubDivisionResult> getClubDivisions() {
        return Arrays.stream(ClubDivision.values())
                .map(ClubDivisionResult::from)
                .toList();
    }

    @Override
    public ClubListResult getClubs(ClubListCommand command, String email) {

        Optional<RootUser> optionalRootUser = rootUserQueryPort.findRootUserByEmail(email);
        Long loginUserId = optionalRootUser.map(RootUser::getId).orElse(null);

        int limit = Math.min(command.size(), 30);

        LocalDateTime now = LocalDateTime.now();

        CursorBasedList<ClubReadModel> cursorBasedList = CursorBasedList.of(
                limit,
                club -> generateCursor(club, command.sortBy(), now),
                searchSize -> clubQueryPort.searchClubs(
                        command.category(),
                        command.divisionList(),
                        command.cursor(),
                        searchSize,
                        command.sortBy(),
                        now
                )
        );

        List<Long> clubIds = cursorBasedList.getContents()
                .stream()
                .map(ClubReadModel::getId)
                .toList();

        Map<Long, Integer> subscriberCountMap = clubQueryPort.countSubscribersByClubIds(clubIds);

        List<Long> subscribedClubIds = List.of();

        if (loginUserId != null && !clubIds.isEmpty()) {
            subscribedClubIds = clubQueryPort.findSubscribedClubIds(clubIds, loginUserId);
        }

        Map<Long, Boolean> subscribedMap = subscribedClubIds.stream()
                .collect(Collectors.toMap(id -> id, id -> true));

        List<ClubItemResult> items =
                cursorBasedList.getContents()
                        .stream()
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


        int totalCount = clubQueryPort.countClubs(command.category(), command.divisionList());

        return new ClubListResult(
                items,
                cursorBasedList.getEndCursor(),
                cursorBasedList.hasNext(),
                totalCount
        );
    }

    @Override
    public ClubDetailResult getClubDetail(Long id, String email) {

        Optional<RootUser> optionalRootUser = rootUserQueryPort.findRootUserByEmail(email);
        Long loginUserId = optionalRootUser.map(RootUser::getId).orElse(null);

        ClubDetailDto dto = clubQueryPort.findClubDetailById(id)
                .orElseThrow(() -> new NotFoundException(CLUB_NOT_FOUND));

        int subscriberCount = clubQueryPort.countSubscribers(id);

        boolean isSubscribed = false;
        if (loginUserId != null) {
            isSubscribed = clubQueryPort.existsSubscription(id, loginUserId);
        }

        ClubDetailResult.Location location = hasLocation(dto) ?
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

    private boolean hasLocation(ClubDetailDto dto) {
        return dto.getBuilding() != null
                || dto.getRoom() != null
                || dto.getLon() != null
                || dto.getLat() != null;
    }

    private String generateCursor(ClubReadModel club, String sortBy, LocalDateTime now) {
        return switch (sortBy) {
            case "name" -> club.getName() + "|" + club.getId();
            case "recruitEndDate" -> {
                int group;
                if (club.getRecruitEndDate() == null) {
                    group = 2;
                } else if (club.getRecruitEndDate().isBefore(now)) {
                    group = 1;
                } else {
                    group = 0;
                }

                String datePart = club.getRecruitEndDate() == null
                        ? "null"
                        : club.getRecruitEndDate().toString();

                yield group + "|" + datePart + "|" + club.getId();
            }
            default -> club.getId().toString();
        };
    }
}
