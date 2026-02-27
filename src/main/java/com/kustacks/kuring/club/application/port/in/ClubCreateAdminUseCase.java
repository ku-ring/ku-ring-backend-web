package com.kustacks.kuring.club.application.port.in;

import com.kustacks.kuring.club.application.port.in.dto.AdminClubCreateCommand;

public interface ClubCreateAdminUseCase {
    void createClub(AdminClubCreateCommand command);
}
