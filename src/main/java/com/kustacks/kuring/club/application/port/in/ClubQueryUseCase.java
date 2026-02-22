package com.kustacks.kuring.club.application.port.in;

import com.kustacks.kuring.club.application.port.in.dto.ClubDetailResult;
import com.kustacks.kuring.club.application.port.in.dto.ClubDivisionResult;
import com.kustacks.kuring.club.application.port.in.dto.ClubListCommand;
import com.kustacks.kuring.club.application.port.in.dto.ClubListResult;

import java.util.List;

public interface ClubQueryUseCase {
    List<ClubDivisionResult> getClubDivisions();

    ClubListResult getClubs(ClubListCommand command, String email);

    ClubDetailResult getClubDetail(Long id, String email);

}
