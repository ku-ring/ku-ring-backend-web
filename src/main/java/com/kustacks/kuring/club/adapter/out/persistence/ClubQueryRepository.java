package com.kustacks.kuring.club.adapter.out.persistence;

import com.kustacks.kuring.club.application.port.out.dto.ClubReadModel;

import java.util.List;

public interface ClubQueryRepository {

    List<ClubReadModel> searchClubs(String category, List<String> divisions, String cursor, int size, String sortBy);

    int countClubs(String category, List<String> divisions);
}
