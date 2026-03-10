package com.kustacks.kuring.club.application.service;

import com.kustacks.kuring.club.application.port.in.ClubSubscriptionUseCase;
import com.kustacks.kuring.club.application.port.in.dto.ClubItemResult;
import com.kustacks.kuring.club.application.port.in.dto.ClubListResult;
import com.kustacks.kuring.club.application.port.in.dto.ClubSubscriptionCommand;
import com.kustacks.kuring.club.application.port.in.dto.SubscribedClubListCommand;
import com.kustacks.kuring.club.application.port.out.ClubQueryPort;
import com.kustacks.kuring.club.application.port.out.ClubSubscriptionCommandPort;
import com.kustacks.kuring.club.application.port.out.ClubSubscriptionQueryPort;
import com.kustacks.kuring.club.application.port.out.dto.ClubReadModel;
import com.kustacks.kuring.club.domain.Club;
import com.kustacks.kuring.common.annotation.UseCase;
import com.kustacks.kuring.common.exception.InvalidStateException;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.common.properties.ServerProperties;
import com.kustacks.kuring.storage.application.port.out.StoragePort;
import com.kustacks.kuring.user.application.port.out.RootUserQueryPort;
import com.kustacks.kuring.user.application.port.out.UserEventPort;
import com.kustacks.kuring.user.application.port.out.UserQueryPort;
import com.kustacks.kuring.user.domain.RootUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@UseCase
@Transactional
@RequiredArgsConstructor
public class ClubCommandService implements ClubSubscriptionUseCase {

    private static final String CLUB_TOPIC_PREFIX = "club.";

    private final ServerProperties serverProperties;
    private final ClubQueryPort clubQueryPort;
    private final ClubSubscriptionCommandPort clubSubscriptionCommandPort;
    private final ClubSubscriptionQueryPort countSubscriptionsQueryPort;
    private final RootUserQueryPort rootUserQueryPort;
    private final UserQueryPort userQueryPort;
    private final UserEventPort userEventPort;
    private final StoragePort storagePort;

    @Override
    public long addSubscription(ClubSubscriptionCommand command) {
        RootUser rootUser = findRootUserByEmail(command.email());
        Club club = findClubById(command.clubId());

        if (isAlreadySubscription(rootUser, club)) {
            throw new InvalidStateException(ErrorCode.CLUB_ALREADY_SUBSCRIBED);
        }

        clubSubscriptionCommandPort.saveSubscription(rootUser, club);
        subscribeAllLoggedInDevices(rootUser.getId(), makeTopic(club));

        return countSubscriptionsQueryPort.countSubscriptions(rootUser.getId());
    }

    @Override
    public long removeSubscription(ClubSubscriptionCommand command) {
        RootUser rootUser = findRootUserByEmail(command.email());
        Club club = findClubById(command.clubId());

        if (!isAlreadySubscription(rootUser, club)) {
            throw new InvalidStateException(ErrorCode.CLUB_NOT_SUBSCRIBED);
        }
        clubSubscriptionCommandPort.deleteSubscription(rootUser, club);
        unsubscribeAllLoggedInDevices(rootUser.getId(), makeTopic(club));

        return countSubscriptionsQueryPort.countSubscriptions(rootUser.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public ClubListResult getSubscribedClubs(SubscribedClubListCommand command) {

        RootUser rootUser = findRootUserByEmail(command.email());

        List<Long> subscribedClubIds = countSubscriptionsQueryPort.findAllSubscribedClubIds(rootUser.getId());

        if (subscribedClubIds.isEmpty()) {
            return new ClubListResult(List.of());
        }

        List<ClubReadModel> clubReadModels = clubQueryPort.findClubsByIds(subscribedClubIds);

        Map<Long, Long> subscriberCountMap = countSubscriptionsQueryPort.countSubscribersByClubIds(subscribedClubIds);

        List<ClubItemResult> clubItemResults = clubReadModels.stream()
                .map(r -> convertClubItemResult(
                        r,
                        true,
                        subscriberCountMap.getOrDefault(r.getId(), 0L)
                ))
                .toList();

        return new ClubListResult(clubItemResults);
    }

    private ClubItemResult convertClubItemResult(
            ClubReadModel clubReadModel,
            boolean isSubscribed,
            Long subscriberCount
    ) {

        String iconImageUrl = null;
        String iconImagePath = clubReadModel.getIconImagePath();

        if (iconImagePath != null && !iconImagePath.isBlank()) {
            iconImageUrl = storagePort.getPresignedUrl(iconImagePath);
        }

        return new ClubItemResult(
                clubReadModel.getId(),
                clubReadModel.getName(),
                clubReadModel.getSummary(),
                iconImageUrl,
                clubReadModel.getCategory().getName(),
                clubReadModel.getDivision().getName(),
                isSubscribed,
                subscriberCount,
                clubReadModel.getRecruitStartDate(),
                clubReadModel.getRecruitEndDate()
        );
    }

    private boolean isAlreadySubscription(RootUser rootUser, Club club) {
        return countSubscriptionsQueryPort.existsSubscription(rootUser.getId(), club.getId());
    }

    private RootUser findRootUserByEmail(String email) {
        return rootUserQueryPort.findRootUserByEmail(email)
                .orElseThrow(() -> new InvalidStateException(ErrorCode.ROOT_USER_NOT_FOUND));
    }

    private Club findClubById(Long id) {
        return clubQueryPort.findClubById(id)
                .orElseThrow(() -> new InvalidStateException(ErrorCode.CLUB_NOT_FOUND));
    }

    private void subscribeAllLoggedInDevices(Long rootUserId, String topic) {
        userQueryPort.findByLoggedInUserId(rootUserId)
                .forEach(user -> userEventPort.subscribeEvent(user.getFcmToken(), topic));

        log.info("동아리 토픽 구독 완료. rootUserId={}, topic={}", rootUserId, topic);
    }

    private void unsubscribeAllLoggedInDevices(Long rootUserId, String topic) {
        userQueryPort.findByLoggedInUserId(rootUserId)
                .forEach(user -> userEventPort.unsubscribeEvent(user.getFcmToken(), topic));

        log.info("동아리 토픽 구독 해제 완료. rootUserId={}, topic={}", rootUserId, topic);
    }

    private String makeTopic(Club club) {
        return serverProperties.ifDevThenAddSuffix(CLUB_TOPIC_PREFIX + club.getId());
    }
}
