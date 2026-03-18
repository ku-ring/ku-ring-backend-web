package com.kustacks.kuring.club.application.port.in;

import com.kustacks.kuring.club.application.port.in.dto.*;

import java.util.*;

public interface ClubQueryUseCase {
    List<ClubDivisionResult> getClubDivisions();

    ClubListResult getClubs(ClubListCommand command);

    ClubListResult getSubscribedClubs(SubscribedClubListCommand command);

    ClubDetailResult getClubDetail(ClubDetailCommand command);
}
