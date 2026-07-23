package com.kustacks.kuring.building.application.port.in;

import com.kustacks.kuring.building.application.port.in.dto.BuildingDetailResult;
import com.kustacks.kuring.building.application.port.in.dto.BuildingSummaryResult;
import com.kustacks.kuring.building.application.port.in.dto.CampusPlaceResult;
import com.kustacks.kuring.building.application.port.in.dto.CategoryResult;

import java.util.List;
import java.util.Optional;

public interface CampusMapQueryUseCase {

    List<CategoryResult> getCategories();

    List<BuildingSummaryResult> getBuildings();

    List<BuildingSummaryResult> searchBuildings(String keyword);

    List<CampusPlaceResult> getCampusPlaces(List<String> categories);

    Optional<BuildingDetailResult> getBuildingDetail(Long buildingId);
}
