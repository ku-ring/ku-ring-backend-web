package com.kustacks.kuring.club.application.port.in;

import com.kustacks.kuring.club.application.port.in.dto.ClubDetailCommand;
import com.kustacks.kuring.club.application.port.in.dto.ClubDetailResult;
import com.kustacks.kuring.club.application.port.in.dto.ClubDivisionResult;
import com.kustacks.kuring.club.application.port.in.dto.ClubListCommand;
import com.kustacks.kuring.club.application.port.in.dto.ClubListResult;
import com.kustacks.kuring.club.application.port.in.dto.SubscribedClubListCommand;

import java.util.List;

public interface ClubQueryUseCase {
    List<ClubDivisionResult> getClubDivisions();

    ClubListResult getClubs(ClubListCommand command);

    ClubListResult getSubscribedClubs(SubscribedClubListCommand command);

    ClubDetailResult getClubDetail(ClubDetailCommand command);
}
