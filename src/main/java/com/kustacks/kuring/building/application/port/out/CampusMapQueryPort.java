package com.kustacks.kuring.building.application.port.out;

import com.kustacks.kuring.building.application.port.out.dto.BuildingDetailReadModel;
import com.kustacks.kuring.building.application.port.out.dto.BuildingSummaryReadModel;
import com.kustacks.kuring.building.application.port.out.dto.CampusPlaceCategoryReadModel;
import com.kustacks.kuring.building.application.port.out.dto.CampusPlaceReadModel;

import java.util.List;
import java.util.Optional;

public interface CampusMapQueryPort {

    List<CampusPlaceCategoryReadModel> findFilterCategories();

    List<BuildingSummaryReadModel> findBuildings();

    List<BuildingSummaryReadModel> searchBuildings(String keyword);

    List<CampusPlaceReadModel> searchCampusPlaces(String keyword);

    List<CampusPlaceReadModel> findCampusPlacesByCategories(List<String> categoryCodes);

    List<CampusPlaceReadModel> findCampusPlacesByBuildingId(Long buildingId);

    Optional<BuildingDetailReadModel> findBuildingById(Long buildingId);
}
