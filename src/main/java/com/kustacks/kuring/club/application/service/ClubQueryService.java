package com.kustacks.kuring.club.application.service;

import com.kustacks.kuring.club.application.port.in.ClubQueryUseCase;
import com.kustacks.kuring.club.application.port.in.dto.ClubDivisionResult;
import com.kustacks.kuring.club.application.port.in.dto.ClubItemResult;
import com.kustacks.kuring.club.application.port.in.dto.ClubListCommand;
import com.kustacks.kuring.club.application.port.in.dto.ClubListResult;
import com.kustacks.kuring.club.application.port.out.ClubQueryPort;
import com.kustacks.kuring.club.application.port.out.dto.ClubReadModel;
import com.kustacks.kuring.club.domain.ClubDivision;
import com.kustacks.kuring.common.annotation.UseCase;
import com.kustacks.kuring.common.data.CursorBasedList;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@UseCase
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ClubQueryService implements ClubQueryUseCase {

    private final ClubQueryPort clubQueryPort;

    @Override
    public List<ClubDivisionResult> getClubDivisions() {
        return Arrays.stream(ClubDivision.values())
                .map(ClubDivisionResult::from)
                .toList();
    }

    @Override
    public ClubListResult getClubs(ClubListCommand command) {

        int limit = Math.min(command.size(), 30);

        CursorBasedList<ClubReadModel> cursorBasedList = CursorBasedList.of(
                limit,
                club -> club.getId().toString(),
                searchSize -> clubQueryPort.searchClubs(
                        command.category(),
                        command.divisionList(),
                        command.cursor().getStringCursor(),
                        searchSize,
                        command.sortBy()
                )
        );

        List<ClubItemResult> items =
                cursorBasedList.getContents()
                        .stream()
                        .map(r -> new ClubItemResult(
                                r.getId(),
                                r.getName(),
                                r.getSummary(),
                                r.getIconImageUrl(),
                                r.getCategory().toLowerCase(),
                                r.getDivision().toLowerCase(),
                                false, // 추후 구독 기능
                                0, // 추후 구독 기능
                                r.getRecruitStartDate(),
                                r.getRecruitEndDate()
                        ))
                        .toList();

        int totalCount = clubQueryPort.countClubs(command.category(), command.divisionList());

        Long nextCursor = cursorBasedList.getEndCursor() == null
                ? null
                : Long.valueOf(cursorBasedList.getEndCursor());

        return new ClubListResult(
                items,
                nextCursor,
                cursorBasedList.hasNext(),
                totalCount
        );
    }
}
