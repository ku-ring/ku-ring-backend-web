package com.kustacks.kuring.building.adapter.out.persistence;

import com.kustacks.kuring.building.application.port.out.CampusMapQueryPort;
import com.kustacks.kuring.building.domain.CampusPlaceCategory;
import com.kustacks.kuring.common.annotation.PersistenceAdapter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@PersistenceAdapter
@RequiredArgsConstructor
public class CampusMapPersistenceAdapter implements CampusMapQueryPort {

    private final CampusPlaceCategoryRepository categoryRepository;

    @Override
    public List<CampusPlaceCategory> findFilterCategories() {
        return categoryRepository.findByFilterEnabledTrueOrderByDisplayOrderAscIdAsc();
    }
}
