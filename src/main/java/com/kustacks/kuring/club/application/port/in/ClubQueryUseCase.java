package com.kustacks.kuring.club.application.port.in;

import com.kustacks.kuring.club.application.port.in.dto.ClubDivisionResult;

import java.util.List;

public interface ClubQueryUseCase {
    List<ClubDivisionResult> getClubDivisions();
}
