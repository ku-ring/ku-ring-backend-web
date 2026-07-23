package com.kustacks.kuring.building.adapter.out.persistence;

import com.kustacks.kuring.building.application.port.out.CampusMapQueryPort;
import com.kustacks.kuring.building.domain.Building;
import com.kustacks.kuring.building.domain.CampusPlace;
import com.kustacks.kuring.building.domain.CampusPlaceCategory;
import com.kustacks.kuring.common.annotation.PersistenceAdapter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@PersistenceAdapter
@RequiredArgsConstructor
public class CampusMapPersistenceAdapter implements CampusMapQueryPort {

    private final BuildingRepository buildingRepository;
    private final CampusPlaceRepository campusPlaceRepository;
    private final CampusPlaceCategoryRepository categoryRepository;

    @Override
    public List<CampusPlaceCategory> findFilterCategories() {
        return categoryRepository.findByFilterEnabledTrueOrderByDisplayOrderAscIdAsc();
    }

    @Override
    public List<Building> findBuildings() {
        return buildingRepository.findAllByOrderByIdAsc();
    }

    @Override
    public List<Building> searchBuildings(String keyword) {
        return buildingRepository.searchByKeyword(keyword);
    }

    @Override
    public List<CampusPlace> findCampusPlacesByCategories(List<String> categoryCodes) {
        if (categoryCodes.isEmpty()) {
            return List.of();
        }
        return campusPlaceRepository.findByFilterCategories(categoryCodes);
    }

    @Override
    public List<CampusPlace> findCampusPlacesByBuildingId(Long buildingId) {
        return campusPlaceRepository.findDistinctByBuildingIdOrderByDisplayOrderAscIdAsc(buildingId);
    }

    @Override
    public Optional<Building> findBuildingById(Long buildingId) {
        return buildingRepository.findById(buildingId);
    }
}
