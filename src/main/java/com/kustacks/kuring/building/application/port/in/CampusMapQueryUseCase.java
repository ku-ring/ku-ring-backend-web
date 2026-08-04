package com.kustacks.kuring.building.application.port.in;

import com.kustacks.kuring.building.application.port.in.dto.BuildingSummaryResult;
import com.kustacks.kuring.building.application.port.in.dto.CategoryResult;

import java.util.List;

public interface CampusMapQueryUseCase {

    List<CategoryResult> getCategories();

    List<BuildingSummaryResult> getBuildings();

    List<BuildingSummaryResult> searchBuildings(String keyword);
}
