package com.kustacks.kuring.club.application.service;

import com.kustacks.kuring.club.application.port.in.ClubQueryUseCase;
import com.kustacks.kuring.club.application.port.in.dto.ClubDivisionResult;
import com.kustacks.kuring.club.domain.ClubDivision;
import com.kustacks.kuring.common.annotation.UseCase;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@UseCase
@Transactional(readOnly = true)
public class ClubQueryService implements ClubQueryUseCase {

    @Override
    public List<ClubDivisionResult> getClubDivisions() {
        return Arrays.stream(ClubDivision.values())
                .map(ClubDivisionResult::from)
                .toList();
    }
}
