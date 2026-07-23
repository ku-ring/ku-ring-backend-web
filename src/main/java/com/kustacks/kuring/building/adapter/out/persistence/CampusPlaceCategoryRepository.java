package com.kustacks.kuring.building.adapter.out.persistence;

import com.kustacks.kuring.building.domain.CampusPlaceCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CampusPlaceCategoryRepository extends JpaRepository<CampusPlaceCategory, Long> {

    List<CampusPlaceCategory> findByFilterEnabledTrueOrderByDisplayOrderAscIdAsc();
}
