package com.kustacks.kuring.club.application.port.out;

import com.kustacks.kuring.club.domain.Club;
import com.kustacks.kuring.user.domain.RootUser;

public interface ClubSubscriptionCommandPort {

    void saveSubscription(RootUser rootUser, Club club);

    void deleteSubscription(RootUser rootUser, Club club);
}
