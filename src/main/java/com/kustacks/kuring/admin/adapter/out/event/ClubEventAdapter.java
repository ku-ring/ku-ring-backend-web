package com.kustacks.kuring.admin.adapter.out.event;

import com.kustacks.kuring.club.application.port.out.ClubEventPort;
import com.kustacks.kuring.common.domain.Events;
import com.kustacks.kuring.common.exception.InvalidStateException;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.storage.adapter.in.event.dto.ClubCreateEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@Component
public class ClubEventAdapter implements ClubEventPort {

    @Override
    public void publishClubCreate(Long clubId, MultipartFile iconImage, MultipartFile posterImage, String iconImagePath, String posterImagePath) {
        try {
            ClubCreateEvent.ClubCreateImage poster = null;
            if (Objects.nonNull(posterImage) && !posterImage.isEmpty()) {
                poster = new ClubCreateEvent.ClubCreateImage(posterImagePath, posterImage);
            }

            Events.raise(new ClubCreateEvent(
                    clubId,
                    new ClubCreateEvent.ClubCreateImage(iconImagePath, iconImage),
                    poster)
            );
        } catch (IOException e) {
            throw new InvalidStateException(ErrorCode.FILE_IO_EXCEPTION);
        }
    }
}
