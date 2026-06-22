package com.kustacks.kuring.club.application.service;

import com.kustacks.kuring.club.application.port.in.ClubCreateAdminUseCase;
import com.kustacks.kuring.club.application.port.in.ClubSubscriptionUseCase;
import com.kustacks.kuring.club.application.port.in.dto.AdminClubCreateCommand;
import com.kustacks.kuring.club.application.port.in.dto.ClubSubscriptionCommand;
import com.kustacks.kuring.club.application.port.out.ClubCommandPort;
import com.kustacks.kuring.club.application.port.out.ClubEventPort;
import com.kustacks.kuring.club.application.port.out.ClubQueryPort;
import com.kustacks.kuring.club.application.port.out.ClubSubscriptionCommandPort;
import com.kustacks.kuring.club.application.port.out.ClubSubscriptionQueryPort;
import com.kustacks.kuring.club.domain.Club;
import com.kustacks.kuring.club.domain.ClubCategory;
import com.kustacks.kuring.club.domain.ClubDivision;
import com.kustacks.kuring.club.domain.ClubSns;
import com.kustacks.kuring.club.domain.ClubSnsType;
import com.kustacks.kuring.common.annotation.UseCase;
import com.kustacks.kuring.common.exception.InvalidStateException;
import com.kustacks.kuring.common.exception.NotFoundException;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.common.properties.ServerProperties;
import com.kustacks.kuring.common.utils.converter.StringToDateTimeConverter;
import com.kustacks.kuring.user.application.port.out.RootUserQueryPort;
import com.kustacks.kuring.user.application.port.out.UserEventPort;
import com.kustacks.kuring.user.application.port.out.UserQueryPort;
import com.kustacks.kuring.user.domain.RootUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.kustacks.kuring.common.exception.code.ErrorCode.API_MISSING_PARAM;
import static com.kustacks.kuring.common.exception.code.ErrorCode.API_INVALID_PARAM;
import static com.kustacks.kuring.common.exception.code.ErrorCode.CLUB_DUPLICATED;

@Slf4j
@UseCase
@Transactional
@RequiredArgsConstructor
public class ClubCommandService implements ClubSubscriptionUseCase, ClubCreateAdminUseCase {

    private static final String CLUB_TOPIC_PREFIX = "club.";
    public static final String CLUB_FILE_PATH_FORMAT = "clubs/%s.%s";

    private final ServerProperties serverProperties;
    private final ClubQueryPort clubQueryPort;
    private final ClubCommandPort clubCommandPort;
    private final ClubSubscriptionCommandPort clubSubscriptionCommandPort;
    private final ClubSubscriptionQueryPort clubSubscriptionQueryPort;
    private final ClubSubscriptionQueryPort countSubscriptionsQueryPort;
    private final ClubEventPort clubEventPort;

    private final RootUserQueryPort rootUserQueryPort;
    private final UserQueryPort userQueryPort;
    private final UserEventPort userEventPort;

    @Override
    public long addSubscription(ClubSubscriptionCommand command) {
        RootUser rootUser = findRootUserByEmail(command.email());
        Club club = findClubById(command.clubId());

        if (isAlreadySubscription(rootUser, club)) {
            throw new InvalidStateException(ErrorCode.CLUB_ALREADY_SUBSCRIBED);
        }

        clubSubscriptionCommandPort.saveSubscription(rootUser, club);
        subscribeAllLoggedInDevices(rootUser.getId(), makeTopic(club));

        return clubSubscriptionQueryPort.countSubscribers(club.getId());
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

        return clubSubscriptionQueryPort.countSubscribers(club.getId());
    }

    @Override
    public void createClub(AdminClubCreateCommand command) {
        //동아리 저장.
        MultipartFile iconImage = command.iconImage();
        validateRequiredIconImage(iconImage);

        ClubCategory category = resolveCategory(command.category());
        ClubDivision division = resolveDivision(command.division());
        LocalDateTime recruitStartAt = null;
        LocalDateTime recruitEndAt = null;
        if (Boolean.FALSE.equals(command.isAlways())) {
            recruitStartAt = parseDateTime(command.recruitStartAt());
            recruitEndAt = parseDateTime(command.recruitEndAt());
        }
        String applyUrl = normalizeOptionalUrl(command.applyUrl());
        String instagramUrl = normalizeOptionalUrl(command.instagramUrl());
        String youtubeUrl = normalizeOptionalUrl(command.youtubeUrl());
        String etcUrl = normalizeOptionalUrl(command.etcUrl());

        String iconImagePath = generateFileName(iconImage);
        String posterImagePath = generateFileName(command.posterImage());

        //유효성 검사.
        validateRecruitmentPeriod(command.isAlways(), recruitStartAt, recruitEndAt);
        validateDuplicateClub(command, division);

        Club clubToSave = createClub(command, category, division, recruitStartAt, recruitEndAt, applyUrl, iconImagePath, posterImagePath);
        Club savedClub = clubCommandPort.save(clubToSave);

        //동아리 SNS 저장.
        List<ClubSns> toSave = makeClubSnsList(savedClub, instagramUrl, youtubeUrl, etcUrl);
        clubCommandPort.saveAll(toSave);

        //이미지 업로드
        clubEventPort.publishClubCreate(savedClub.getId(), iconImage, command.posterImage(), iconImagePath, posterImagePath);

    }

    private static Club createClub(AdminClubCreateCommand command, ClubCategory category, ClubDivision division, LocalDateTime recruitStartAt, LocalDateTime recruitEndAt, String applyUrl, String iconImagePath, String posterImagePath) {
        return new Club(
                command.name(), command.summary(), command.description(),
                category, division, command.building(), command.room(), command.lat(), command.lon(),
                recruitStartAt, recruitEndAt,
                command.isAlways(), applyUrl, command.qualifications(),
                iconImagePath, posterImagePath
        );
    }

    private void validateDuplicateClub(AdminClubCreateCommand command, ClubDivision division) {
        if (clubQueryPort.existsByNameAndDivision(command.name(), division)) {
            throw new InvalidStateException(CLUB_DUPLICATED);
        }
    }

    private List<ClubSns> makeClubSnsList(Club club, String... urls) {
        return Arrays.stream(urls)
                .filter(StringUtils::hasText)
                .map(url -> new ClubSns(ClubSnsType.fromUrl(url), url, club))
                .toList();
    }

    private void validateRecruitmentPeriod(Boolean isAlways, LocalDateTime recruitStartAt, LocalDateTime recruitEndAt) {
        //상시 모집이 아닌데, 모집시작/종료일이 없는 경우
        if (Boolean.FALSE.equals(isAlways) &&
                (recruitStartAt == null || recruitEndAt == null || recruitStartAt.isAfter(recruitEndAt))) {
            throw new InvalidStateException(API_INVALID_PARAM);
        }
    }

    private ClubCategory resolveCategory(String category) {
        try {
            return ClubCategory.fromName(category);
        } catch (NotFoundException ignored) {
            return ClubCategory.fromKorName(category);
        }
    }

    private ClubDivision resolveDivision(String division) {
        try {
            return ClubDivision.fromName(division);
        } catch (NotFoundException ignored) {
            return ClubDivision.fromKorName(division);
        }
    }

    private LocalDateTime parseDateTime(String value) {
        try {
            return StringToDateTimeConverter.convert(value);
        } catch (RuntimeException e) {
            throw new InvalidStateException(API_INVALID_PARAM);
        }
    }

    private void validateRequiredIconImage(MultipartFile iconImage) {
        if (iconImage == null || iconImage.isEmpty()) {
            throw new InvalidStateException(API_MISSING_PARAM);
        }
    }

    private String normalizeOptionalUrl(String url) {
        if (!StringUtils.hasText(url)) {
            return null;
        }

        String trimUrl = url.trim();
        String normalizedUrl;
        if (trimUrl.startsWith("https://")) {
            normalizedUrl = trimUrl;
        } else if (trimUrl.startsWith("http://")) {
            normalizedUrl = "https://" + trimUrl.substring("http://".length());
        } else {
            throw new InvalidStateException(API_INVALID_PARAM);
        }

        if (normalizedUrl.length() > 255) {
            throw new InvalidStateException(API_INVALID_PARAM);
        }
        return normalizedUrl;
    }

    private String generateFileName(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        String originalFilename = file.getOriginalFilename();
        String extension = extractExtension(originalFilename);
        return String.format(
                CLUB_FILE_PATH_FORMAT,
                UUID.randomUUID(), extension
        );
    }

    private String extractExtension(String originalFilename) {
        if (!StringUtils.hasText(originalFilename)) {
            throw new InvalidStateException(API_INVALID_PARAM);
        }

        int index = originalFilename.lastIndexOf('.');
        if (index <= 0 || index == originalFilename.length() - 1) {
            throw new InvalidStateException(API_INVALID_PARAM);
        }
        return originalFilename.substring(index + 1).toLowerCase();
    }

    @Override
    public void createClub(AdminClubCreateCommand command) {
        //동아리 저장.
        MultipartFile iconImage = command.iconImage();
        validateRequiredIconImage(iconImage);

        ClubCategory category = resolveCategory(command.category());
        ClubDivision division = resolveDivision(command.division());
        LocalDateTime recruitStartAt = null;
        LocalDateTime recruitEndAt = null;
        if (Boolean.FALSE.equals(command.isAlways())) {
            recruitStartAt = parseDateTime(command.recruitStartAt());
            recruitEndAt = parseDateTime(command.recruitEndAt());
        }
        String applyUrl = normalizeOptionalUrl(command.applyUrl());
        String instagramUrl = normalizeOptionalUrl(command.instagramUrl());
        String youtubeUrl = normalizeOptionalUrl(command.youtubeUrl());
        String etcUrl = normalizeOptionalUrl(command.etcUrl());

        String iconImagePath = generateFileName(iconImage);
        String posterImagePath = generateFileName(command.posterImage());

        //유효성 검사.
        validateRecruitmentPeriod(command.isAlways(), recruitStartAt, recruitEndAt);
        validateDuplicateClub(command, division);

        Club clubToSave = createClub(command, category, division, recruitStartAt, recruitEndAt, applyUrl, iconImagePath, posterImagePath);
        Club savedClub = clubCommandPort.save(clubToSave);

        //동아리 SNS 저장.
        List<ClubSns> toSave = makeClubSnsList(savedClub, instagramUrl, youtubeUrl, etcUrl);
        clubCommandPort.saveAll(toSave);

        //이미지 업로드
        clubEventPort.publishClubCreate(savedClub.getId(), iconImage, command.posterImage(), iconImagePath, posterImagePath);

    }

    private static Club createClub(AdminClubCreateCommand command, ClubCategory category, ClubDivision division, LocalDateTime recruitStartAt, LocalDateTime recruitEndAt, String applyUrl, String iconImagePath, String posterImagePath) {
        return new Club(
                command.name(), command.summary(), command.description(),
                category, division, command.building(), command.room(), command.lat(), command.lon(),
                recruitStartAt, recruitEndAt,
                command.isAlways(), applyUrl, command.qualifications(),
                iconImagePath, posterImagePath
        );
    }

    private void validateDuplicateClub(AdminClubCreateCommand command, ClubDivision division) {
        if (clubQueryPort.existsByNameAndDivision(command.name(), division)) {
            throw new InvalidStateException(CLUB_DUPLICATED);
        }
    }

    private List<ClubSns> makeClubSnsList(Club club, String... urls) {
        return Arrays.stream(urls)
                .filter(StringUtils::hasText)
                .map(url -> new ClubSns(ClubSnsType.fromUrl(url), url, club))
                .toList();
    }

    private void validateRecruitmentPeriod(Boolean isAlways, LocalDateTime recruitStartAt, LocalDateTime recruitEndAt) {
        //상시 모집이 아닌데, 모집시작/종료일이 없는 경우
        if (Boolean.FALSE.equals(isAlways) &&
                (recruitStartAt == null || recruitEndAt == null || recruitStartAt.isAfter(recruitEndAt))) {
            throw new InvalidStateException(API_INVALID_PARAM);
        }
    }

    private ClubCategory resolveCategory(String category) {
        try {
            return ClubCategory.fromName(category);
        } catch (NotFoundException ignored) {
            return ClubCategory.fromKorName(category);
        }
    }

    private ClubDivision resolveDivision(String division) {
        try {
            return ClubDivision.fromName(division);
        } catch (NotFoundException ignored) {
            return ClubDivision.fromKorName(division);
        }
    }

    private LocalDateTime parseDateTime(String value) {
        try {
            return StringToDateTimeConverter.convert(value);
        } catch (RuntimeException e) {
            throw new InvalidStateException(API_INVALID_PARAM);
        }
    }

    private void validateRequiredIconImage(MultipartFile iconImage) {
        if (iconImage == null || iconImage.isEmpty()) {
            throw new InvalidStateException(API_MISSING_PARAM);
        }
    }

    private String normalizeOptionalUrl(String url) {
        if (!StringUtils.hasText(url)) {
            return null;
        }

        String trimUrl = url.trim();
        String normalizedUrl;
        if (trimUrl.startsWith("https://")) {
            normalizedUrl = trimUrl;
        } else if (trimUrl.startsWith("http://")) {
            normalizedUrl = "https://" + trimUrl.substring("http://".length());
        } else {
            throw new InvalidStateException(API_INVALID_PARAM);
        }

        if (normalizedUrl.length() > 255) {
            throw new InvalidStateException(API_INVALID_PARAM);
        }
        return normalizedUrl;
    }

    private String generateFileName(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        String originalFilename = file.getOriginalFilename();
        String extension = extractExtension(originalFilename);
        return String.format(
                CLUB_FILE_PATH_FORMAT,
                UUID.randomUUID(), extension
        );
    }

    private String extractExtension(String originalFilename) {
        if (!StringUtils.hasText(originalFilename)) {
            throw new InvalidStateException(API_INVALID_PARAM);
        }

        int index = originalFilename.lastIndexOf('.');
        if (index <= 0 || index == originalFilename.length() - 1) {
            throw new InvalidStateException(API_INVALID_PARAM);
        }
        return originalFilename.substring(index + 1).toLowerCase();
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
        return CLUB_TOPIC_PREFIX + club.getId();
    }
}
