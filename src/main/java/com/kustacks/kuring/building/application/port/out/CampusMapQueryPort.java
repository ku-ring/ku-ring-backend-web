package com.kustacks.kuring.building.application.port.out;

import com.kustacks.kuring.building.domain.Building;
import com.kustacks.kuring.building.domain.CampusPlace;
import com.kustacks.kuring.building.domain.CampusPlaceCategory;

import java.util.List;
import java.util.Optional;

public interface CampusMapQueryPort {

    List<CampusPlaceCategory> findFilterCategories();

    List<Building> findBuildings();

    List<Building> searchBuildings(String keyword);

    List<CampusPlace> findCampusPlacesByCategories(List<String> categoryCodes);

    List<CampusPlace> findCampusPlacesByBuildingId(Long buildingId);

    Optional<Building> findBuildingById(Long buildingId);
}
