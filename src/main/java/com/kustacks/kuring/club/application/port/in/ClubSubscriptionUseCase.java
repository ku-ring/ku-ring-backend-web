package com.kustacks.kuring.club.application.port.in;

import com.kustacks.kuring.club.application.port.in.dto.ClubListResult;
import com.kustacks.kuring.club.application.port.in.dto.ClubSubscriptionCommand;
import com.kustacks.kuring.club.application.port.in.dto.SubscribedClubListCommand;

public interface ClubSubscriptionUseCase {

    long addSubscription(ClubSubscriptionCommand command);

    long removeSubscription(ClubSubscriptionCommand command);

    ClubListResult getSubscribedClubs(SubscribedClubListCommand command);
}
