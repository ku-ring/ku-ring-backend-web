package com.kustacks.kuring.building.application.port.out;

import com.kustacks.kuring.building.application.port.out.dto.BuildingSummaryReadModel;
import com.kustacks.kuring.building.application.port.out.dto.CampusPlaceCategoryReadModel;

import java.util.List;

public interface CampusMapQueryPort {

    List<CampusPlaceCategoryReadModel> findFilterCategories();

    List<BuildingSummaryReadModel> findBuildings();

    List<BuildingSummaryReadModel> searchBuildings(String keyword);
}
