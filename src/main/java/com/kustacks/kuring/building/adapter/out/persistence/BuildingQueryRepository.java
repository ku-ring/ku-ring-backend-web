package com.kustacks.kuring.building.adapter.out.persistence;

import com.kustacks.kuring.building.domain.Building;

import java.util.List;

public interface BuildingQueryRepository {

    List<Building> searchByKeyword(String keyword);
}
