package com.kustacks.kuring.building.application.port.out;

import com.kustacks.kuring.building.domain.CampusPlaceCategory;

import java.util.List;

public interface CampusMapQueryPort {

    List<CampusPlaceCategory> findFilterCategories();
}
