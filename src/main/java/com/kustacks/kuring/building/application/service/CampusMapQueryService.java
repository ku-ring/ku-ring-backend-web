package com.kustacks.kuring.building.application.service;

import com.kustacks.kuring.building.application.port.in.CampusMapQueryUseCase;
import com.kustacks.kuring.building.application.port.in.dto.BuildingSummaryResult;
import com.kustacks.kuring.building.application.port.in.dto.CategoryResult;
import com.kustacks.kuring.building.application.port.out.CampusMapQueryPort;
import com.kustacks.kuring.building.application.port.out.dto.BuildingSummaryReadModel;
import com.kustacks.kuring.building.application.port.out.dto.CampusPlaceCategoryReadModel;
import com.kustacks.kuring.common.annotation.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@UseCase
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CampusMapQueryService implements CampusMapQueryUseCase {

    private final CampusMapQueryPort campusMapQueryPort;

    @Override
    public List<CategoryResult> getCategories() {
        return campusMapQueryPort.findFilterCategories().stream()
                .map(this::toCategoryResult)
                .toList();
    }

    @Override
    public List<BuildingSummaryResult> getBuildings() {
        return campusMapQueryPort.findBuildings().stream()
                .map(this::toBuildingSummaryResult)
                .toList();
    }

    @Override
    public List<BuildingSummaryResult> searchBuildings(String keyword) {
        String normalizedKeyword = keyword.trim();

        return campusMapQueryPort.searchBuildings(normalizedKeyword).stream()
                .map(this::toBuildingSummaryResult)
                .toList();
    }

    private CategoryResult toCategoryResult(CampusPlaceCategoryReadModel category) {
        return new CategoryResult(
                category.code(),
                category.korName(),
                category.displayOrder()
        );
    }

    private BuildingSummaryResult toBuildingSummaryResult(BuildingSummaryReadModel building) {
        return new BuildingSummaryResult(
                building.id(),
                building.name(),
                building.address(),
                building.latitude(),
                building.longitude()
        );
    }
}
