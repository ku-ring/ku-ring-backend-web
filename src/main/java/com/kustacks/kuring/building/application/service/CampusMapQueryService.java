package com.kustacks.kuring.building.application.service;

import com.kustacks.kuring.building.application.port.in.CampusMapQueryUseCase;
import com.kustacks.kuring.building.application.port.in.dto.CategoryResult;
import com.kustacks.kuring.building.application.port.out.CampusMapQueryPort;
import com.kustacks.kuring.building.domain.CampusPlaceCategory;
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

    private CategoryResult toCategoryResult(CampusPlaceCategory category) {
        return new CategoryResult(
                category.getCode(),
                category.getKorName(),
                category.getDisplayOrder()
        );
    }
}
