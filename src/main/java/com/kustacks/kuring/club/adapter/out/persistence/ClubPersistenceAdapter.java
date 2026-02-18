package com.kustacks.kuring.club.adapter.out.persistence;

import com.kustacks.kuring.club.application.port.out.ClubQueryPort;
import com.kustacks.kuring.club.application.port.out.dto.ClubReadModel;
import com.kustacks.kuring.common.annotation.PersistenceAdapter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@PersistenceAdapter
@RequiredArgsConstructor
public class ClubPersistenceAdapter implements ClubQueryPort {

    private final ClubRepository clubRepository;

    @Override
    public List<ClubReadModel> searchClubs(
            String category,
            List<String> divisions,
            String cursor,
            int size,
            String sortBy
    ) {
        return clubRepository.searchClubs(category, divisions, cursor, size, sortBy);
    }

    @Override
    public int countClubs(String category, List<String> divisions) {
        return clubRepository.countClubs(category, divisions);
    }
}
