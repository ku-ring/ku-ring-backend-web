package com.kustacks.kuring.building.adapter.out.persistence;

import com.kustacks.kuring.building.domain.CampusPlace;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CampusPlaceRepository extends JpaRepository<CampusPlace, Long> {

    @EntityGraph(attributePaths = {"building", "category", "operatingHours"})
    @Query("""
            select distinct place
            from CampusPlace place
            where place.category.code in :categoryCodes
              and place.category.filterEnabled = true
            order by place.displayOrder asc, place.id asc
            """)
    List<CampusPlace> findByFilterCategories(@Param("categoryCodes") List<String> categoryCodes);

    @EntityGraph(attributePaths = {"building", "category", "operatingHours"})
    List<CampusPlace> findDistinctByBuildingIdOrderByDisplayOrderAscIdAsc(Long buildingId);
}
