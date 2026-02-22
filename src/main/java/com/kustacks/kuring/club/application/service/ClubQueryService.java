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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

        CursorBasedList<ClubReadModel> cursorBasedList = CursorBasedList.of(
                limit,
                club -> generateCursor(club, command.sortBy()),
                searchSize -> clubQueryPort.searchClubs(
                        command.category(),
                        command.divisionList(),
                        command.cursor(),
                        searchSize,
                        command.sortBy()
                )
        );

        List<ClubItemResult> items =
                cursorBasedList.getContents()
                        .stream()
                        .map(r -> {

                            int subscriberCount =
                                    clubQueryPort.countSubscribers(r.getId());

                            boolean isSubscribed = false;
                            if (loginUserId != null) {
                                isSubscribed = clubQueryPort.existsSubscription(r.getId(), loginUserId);
                            }

                            return new ClubItemResult(
                                    r.getId(),
                                    r.getName(),
                                    r.getSummary(),
                                    r.getIconImageUrl(),
                                    r.getCategory().getName(),
                                    r.getDivision().getName(),
                                    isSubscribed,
                                    subscriberCount,
                                    r.getRecruitStartDate(),
                                    r.getRecruitEndDate()
                            );
                        })
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

    private String generateCursor(ClubReadModel club, String sortBy) {
        return switch (sortBy) {
            case "name" -> club.getName() + "|" + club.getId();
            case "recruitEndDate" -> {
                if (club.getRecruitEndDate() == null) {
                    yield "null|" + club.getId();
                }
                yield club.getRecruitEndDate()
                        + "|" + club.getId();
            }
            default -> club.getId().toString();
        };
    }
}
