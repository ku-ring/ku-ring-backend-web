package com.kustacks.kuring.club.application.port.out;

import java.util.List;
import java.util.Map;

public interface ClubSubscriptionQueryPort {

    boolean existsSubscription(Long rootUserId, Long clubId);

    Long countSubscriptions(Long rootUserId);

    List<Long> findSubscribedClubIds(List<Long> clubIds, Long rootUserId);

    Long countSubscribers(Long clubId);

    Map<Long, Long> countSubscribersByClubIds(List<Long> clubIds);

}
