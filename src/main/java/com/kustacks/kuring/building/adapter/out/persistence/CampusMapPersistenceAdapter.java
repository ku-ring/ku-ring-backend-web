package com.kustacks.kuring.building.adapter.out.persistence;

import com.kustacks.kuring.building.application.port.out.CampusMapQueryPort;
import com.kustacks.kuring.building.application.port.out.dto.BuildingSummaryReadModel;
import com.kustacks.kuring.building.application.port.out.dto.CampusPlaceCategoryReadModel;
import com.kustacks.kuring.building.application.port.out.dto.CampusPlaceReadModel;
import com.kustacks.kuring.building.application.port.out.dto.OperatingHoursReadModel;
import com.kustacks.kuring.building.domain.Building;
import com.kustacks.kuring.building.domain.CampusPlace;
import com.kustacks.kuring.building.domain.OperatingHours;
import com.kustacks.kuring.common.annotation.PersistenceAdapter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@PersistenceAdapter
@RequiredArgsConstructor
public class CampusMapPersistenceAdapter implements CampusMapQueryPort {

    private final BuildingRepository buildingRepository;
    private final CampusPlaceRepository campusPlaceRepository;
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

    @Override
    public List<BuildingSummaryReadModel> searchBuildings(String keyword) {
        return buildingRepository.searchByKeyword(keyword).stream()
                .map(this::toBuildingSummaryReadModel)
                .toList();
    }

    @Override
    public List<CampusPlaceReadModel> findCampusPlacesByCategories(List<String> categoryCodes) {
        if (categoryCodes.isEmpty()) {
            return List.of();
        }

        return campusPlaceRepository.findByFilterCategories(categoryCodes).stream()
                .map(this::toCampusPlaceReadModel)
                .toList();
    }

    private CampusPlaceReadModel toCampusPlaceReadModel(CampusPlace place) {
        return new CampusPlaceReadModel(
                place.getId(),
                place.getName(),
                place.getCategory().getCode(),
                place.getCategory().getKorName(),
                place.getImagePath(),
                place.getLocationType(),
                place.getFloor(),
                place.getLocationDetail(),
                place.getQuantity(),
                place.getOperatingHours().stream()
                        .map(this::toOperatingHoursReadModel)
                        .toList(),
                place.getExternalUrl(),
                toBuildingSummaryReadModel(place.getBuilding())
        );
    }

    private OperatingHoursReadModel toOperatingHoursReadModel(OperatingHours operatingHours) {
        return new OperatingHoursReadModel(
                operatingHours.getPeriod(),
                operatingHours.getDayGroup(),
                operatingHours.getStatus(),
                operatingHours.getOpensAt(),
                operatingHours.getClosesAt()
        );
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
