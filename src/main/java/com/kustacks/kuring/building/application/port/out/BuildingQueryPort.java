package com.kustacks.kuring.building.application.port.out;

import com.kustacks.kuring.building.domain.Building;

import java.util.Optional;

public interface BuildingQueryPort {

    Optional<Building> findById(Long id);
}