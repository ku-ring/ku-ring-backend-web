package com.kustacks.kuring.building.application.port.out;

import com.kustacks.kuring.building.application.port.out.dto.CampusPlaceCategoryReadModel;
import com.kustacks.kuring.building.domain.Building;

import java.util.List;

public interface CampusMapQueryPort {

    List<CampusPlaceCategoryReadModel> findFilterCategories();

    List<Building> findBuildings();
}
