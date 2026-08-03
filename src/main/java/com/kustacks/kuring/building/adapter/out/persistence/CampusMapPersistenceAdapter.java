package com.kustacks.kuring.building.adapter.out.persistence;

import com.kustacks.kuring.building.application.port.out.CampusMapQueryPort;
import com.kustacks.kuring.building.application.port.out.dto.BuildingSummaryReadModel;
import com.kustacks.kuring.building.application.port.out.dto.CampusPlaceCategoryReadModel;
import com.kustacks.kuring.building.domain.Building;
import com.kustacks.kuring.common.annotation.PersistenceAdapter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@PersistenceAdapter
@RequiredArgsConstructor
public class CampusMapPersistenceAdapter implements CampusMapQueryPort {

    private final BuildingRepository buildingRepository;
    private final CampusPlaceCategoryRepository categoryRepository;

    @Override
    public List<CampusPlaceCategoryReadModel> findFilterCategories() {
        return categoryRepository.findByFilterEnabledTrueOrderByDisplayOrderAscIdAsc().stream()
                .map(category -> new CampusPlaceCategoryReadModel(
                        category.getCode(),
                        category.getKorName(),
                        category.getDisplayOrder()
                ))
                .toList();
    }

    @Override
    public List<BuildingSummaryReadModel> findBuildings() {
        return buildingRepository.findAllByOrderByIdAsc().stream()
                .map(this::toBuildingSummaryReadModel)
                .toList();
    }

    private BuildingSummaryReadModel toBuildingSummaryReadModel(Building building) {
        return new BuildingSummaryReadModel(
                building.getId(),
                building.getName(),
                building.getAddress(),
                building.getLat(),
                building.getLon()
        );
    }
}
