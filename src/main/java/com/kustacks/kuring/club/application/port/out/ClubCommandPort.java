package com.kustacks.kuring.club.application.port.out;

import com.kustacks.kuring.club.domain.Club;
import com.kustacks.kuring.club.domain.ClubSns;

import java.util.List;

public interface ClubCommandPort {
    Club save(Club club);

    void saveAll(List<ClubSns> toSave);
}
