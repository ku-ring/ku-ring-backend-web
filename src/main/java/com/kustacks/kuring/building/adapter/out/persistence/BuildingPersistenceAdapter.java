package com.kustacks.kuring.building.adapter.out.persistence;

import com.kustacks.kuring.building.application.port.out.BuildingQueryPort;
import com.kustacks.kuring.building.domain.Building;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BuildingPersistenceAdapter implements BuildingQueryPort {

    private final BuildingRepository buildingRepository;

    @Override
    public Optional<Building> findById(Long id) {
        return buildingRepository.findById(id);
    }
}