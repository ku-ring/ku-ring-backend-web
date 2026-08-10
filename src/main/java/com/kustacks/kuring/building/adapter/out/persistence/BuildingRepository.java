package com.kustacks.kuring.building.adapter.out.persistence;

import com.kustacks.kuring.building.domain.Building;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BuildingRepository extends JpaRepository<Building, Long>, BuildingQueryRepository {

    List<Building> findAllByOrderByDisplayOrderAscIdAsc();
}
