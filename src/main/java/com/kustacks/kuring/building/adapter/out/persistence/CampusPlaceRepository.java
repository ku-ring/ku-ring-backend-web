package com.kustacks.kuring.building.adapter.out.persistence;

import com.kustacks.kuring.building.domain.CampusPlace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampusPlaceRepository extends JpaRepository<CampusPlace, Long>, CampusPlaceQueryRepository {
}
