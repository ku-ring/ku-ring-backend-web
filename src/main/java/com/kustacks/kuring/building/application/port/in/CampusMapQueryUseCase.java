package com.kustacks.kuring.building.application.port.in;

import com.kustacks.kuring.building.application.port.in.dto.BuildingDetailResult;
import com.kustacks.kuring.building.application.port.in.dto.BuildingOverviewResult;
import com.kustacks.kuring.building.application.port.in.dto.CampusPlaceResult;
import com.kustacks.kuring.building.application.port.in.dto.CampusMapSearchResult;
import com.kustacks.kuring.building.application.port.in.dto.CategoryResult;

import java.util.List;

public interface CampusMapQueryUseCase {

    List<CategoryResult> getCategories();

    List<BuildingOverviewResult> getBuildings();

    CampusMapSearchResult searchCampusMap(String keyword);

    List<CampusPlaceResult> getCampusPlaces(List<String> categories);

    BuildingDetailResult getBuildingDetail(Long buildingId);
}
