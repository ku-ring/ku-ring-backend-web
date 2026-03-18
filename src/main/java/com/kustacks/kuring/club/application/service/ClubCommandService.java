package com.kustacks.kuring.club.application.service;

import com.kustacks.kuring.club.application.port.in.*;
import com.kustacks.kuring.club.application.port.in.dto.*;
import com.kustacks.kuring.club.application.port.out.*;
import com.kustacks.kuring.club.domain.*;
import com.kustacks.kuring.common.annotation.*;
import com.kustacks.kuring.common.exception.*;
import com.kustacks.kuring.common.exception.code.*;
import com.kustacks.kuring.common.properties.*;
import com.kustacks.kuring.storage.application.port.out.*;
import com.kustacks.kuring.user.application.port.out.*;
import com.kustacks.kuring.user.domain.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.transaction.annotation.*;

@Slf4j
@UseCase
@Transactional
@RequiredArgsConstructor
public class ClubCommandService implements ClubSubscriptionUseCase {

    private static final String CLUB_TOPIC_PREFIX = "club.";

    private final ServerProperties serverProperties;
    private final ClubQueryPort clubQueryPort;
    private final ClubSubscriptionCommandPort clubSubscriptionCommandPort;
    private final ClubSubscriptionQueryPort clubSubscriptionQueryPort;
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

        return clubSubscriptionQueryPort.countSubscriptions(rootUser.getId());
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

        return clubSubscriptionQueryPort.countSubscriptions(rootUser.getId());
    }

    private boolean isAlreadySubscription(RootUser rootUser, Club club) {
        return clubSubscriptionQueryPort.existsSubscription(rootUser.getId(), club.getId());
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
