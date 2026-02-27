package com.kustacks.kuring.club.application.port.out;

import org.springframework.web.multipart.MultipartFile;

public interface ClubEventPort {
    void publishClubCreate(Long clubId, MultipartFile iconImage, MultipartFile posterImage, String iconImagePath, String posterImagePath);
}
