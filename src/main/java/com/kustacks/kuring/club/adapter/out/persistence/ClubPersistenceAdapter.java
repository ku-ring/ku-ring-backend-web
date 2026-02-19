package com.kustacks.kuring.club.adapter.out.persistence;

import com.kustacks.kuring.club.application.port.out.ClubQueryPort;
import com.kustacks.kuring.club.application.port.out.dto.ClubReadModel;
import com.kustacks.kuring.common.annotation.PersistenceAdapter;
import com.kustacks.kuring.common.data.Cursor;
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
            Cursor cursor,
            int size,
            String sortBy
    ) {
        return clubRepository.searchClubs(
                category,
                divisions,
                cursor == null ? null : cursor.getStringCursor(),
                size,
                sortBy
        );
    }

    @Override
    public int countClubs(String category, List<String> divisions) {
        return clubRepository.countClubs(category, divisions);
    }
}
