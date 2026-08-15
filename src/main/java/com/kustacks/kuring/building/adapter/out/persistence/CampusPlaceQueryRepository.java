package com.kustacks.kuring.building.adapter.out.persistence;

import com.kustacks.kuring.building.domain.CampusPlace;

import java.util.List;

public interface CampusPlaceQueryRepository {

    List<CampusPlace> searchByKeyword(String keyword);

    List<CampusPlace> findByFilterCategories(List<String> categoryCodes);

    List<CampusPlace> findByBuildingId(Long buildingId);
}
