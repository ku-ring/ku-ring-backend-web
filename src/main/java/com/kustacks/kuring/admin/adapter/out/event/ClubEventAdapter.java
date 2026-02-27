package com.kustacks.kuring.admin.adapter.out.event;

import com.kustacks.kuring.club.application.port.out.ClubEventPort;
import com.kustacks.kuring.common.domain.Events;
import com.kustacks.kuring.common.exception.InvalidStateException;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.storage.adapter.in.event.dto.ClubCreateEvent;
import com.kustacks.kuring.storage.adapter.in.event.dto.ClubCreateEvent.ClubCreateImage;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class ClubEventAdapter implements ClubEventPort {

    @Override
    public void publishClubCreate(Long clubId, MultipartFile iconImage, MultipartFile posterImage, String iconImagePath, String posterImagePath) {
        try {
            ClubCreateImage icon = new ClubCreateImage(iconImagePath, iconImage);

            if (posterImage != null) {
                ClubCreateImage poster = new ClubCreateImage(posterImagePath, posterImage);
                Events.raise(new ClubCreateEvent(clubId, icon, poster));
            } else {
                Events.raise(new ClubCreateEvent(clubId, icon, null));
            }

        } catch (IOException e) {
            throw new InvalidStateException(ErrorCode.FILE_IO_EXCEPTION);
        }
    }
}
