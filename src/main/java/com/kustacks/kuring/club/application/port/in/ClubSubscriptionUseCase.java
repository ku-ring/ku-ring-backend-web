package com.kustacks.kuring.club.application.port.in;

import com.kustacks.kuring.club.application.port.in.dto.ClubSubscriptionCommand;

public interface ClubSubscriptionUseCase {

    long addSubscription(ClubSubscriptionCommand command);

    long removeSubscription(ClubSubscriptionCommand command);
}
