package com.kustacks.kuring.club.application.port.out;

public interface ClubSubscriptionQueryPort {

    boolean existsSubscription(Long rootUserId, Long clubId);

    long countSubscriptions(Long rootUserId);
}
