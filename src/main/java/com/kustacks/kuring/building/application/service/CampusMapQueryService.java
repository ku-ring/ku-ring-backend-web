package com.kustacks.kuring.building.application.service;

import com.kustacks.kuring.building.application.port.in.CampusMapQueryUseCase;
import com.kustacks.kuring.building.application.port.in.dto.BuildingDetailResult;
import com.kustacks.kuring.building.application.port.in.dto.BuildingSummaryResult;
import com.kustacks.kuring.building.application.port.in.dto.CampusPlaceResult;
import com.kustacks.kuring.building.application.port.in.dto.CategoryResult;
import com.kustacks.kuring.building.application.port.in.dto.OperatingHoursResult;
import com.kustacks.kuring.building.application.port.out.AcademicPeriodQueryPort;
import com.kustacks.kuring.building.application.port.out.CampusMapQueryPort;
import com.kustacks.kuring.building.application.port.out.dto.BuildingDetailReadModel;
import com.kustacks.kuring.building.application.port.out.dto.BuildingSummaryReadModel;
import com.kustacks.kuring.building.application.port.out.dto.CampusPlaceCategoryReadModel;
import com.kustacks.kuring.building.application.port.out.dto.CampusPlaceReadModel;
import com.kustacks.kuring.building.application.port.out.dto.OperatingHoursReadModel;
import com.kustacks.kuring.building.application.service.model.OperatingContext;
import com.kustacks.kuring.building.domain.OperatingDayGroup;
import com.kustacks.kuring.building.domain.OperatingPeriod;
import com.kustacks.kuring.common.annotation.UseCase;
import com.kustacks.kuring.common.exception.NotFoundException;
import com.kustacks.kuring.storage.application.port.out.StoragePort;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import static com.kustacks.kuring.common.exception.code.ErrorCode.BUILDING_NOT_FOUND;
import static com.kustacks.kuring.common.utils.TimeUtils.isWeekend;

@UseCase
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CampusMapQueryService implements CampusMapQueryUseCase {

    private final CampusMapQueryPort campusMapQueryPort;
    private final AcademicPeriodQueryPort academicPeriodQueryPort;
    private final StoragePort storagePort;
    private final Clock clock;

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

    @Override
    public List<CampusPlaceResult> getCampusPlaces(List<String> categories) {
        List<String> normalizedCategories = normalizeCategories(categories);
        OperatingContext context = currentOperatingContext();

        return campusMapQueryPort.findCampusPlacesByCategories(normalizedCategories).stream()
                .map(place -> toCampusPlaceResult(place, context))
                .toList();
    }

    @Override
    public BuildingDetailResult getBuildingDetail(Long buildingId) {
        BuildingDetailReadModel building = campusMapQueryPort.findBuildingById(buildingId)
                .orElseThrow(() -> new NotFoundException(BUILDING_NOT_FOUND));

        return toBuildingDetailResult(
                building,
                campusMapQueryPort.findCampusPlacesByBuildingId(buildingId),
                currentOperatingContext()
        );
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

    private CampusPlaceResult toCampusPlaceResult(
            CampusPlaceReadModel place,
            OperatingContext context
    ) {
        return new CampusPlaceResult(
                place.id(),
                place.name(),
                place.categoryCode(),
                place.categoryKorName(),
                resolveImageUrl(place.imagePath()),
                place.locationType(),
                place.floor(),
                place.locationDetail(),
                place.quantity(),
                resolveOperatingHours(place.operatingHours(), context),
                place.externalUrl(),
                toBuildingSummaryResult(place.building())
        );
    }

    private BuildingDetailResult toBuildingDetailResult(
            BuildingDetailReadModel building,
            List<CampusPlaceReadModel> campusPlaces,
            OperatingContext context
    ) {
        return new BuildingDetailResult(
                building.id(),
                building.name(),
                building.address(),
                building.latitude(),
                building.longitude(),
                resolveImageUrl(building.imagePath()),
                resolveOperatingHours(building.operatingHours(), context),
                campusPlaces.stream()
                        .map(place -> toCampusPlaceResult(place, context))
                        .toList()
        );
    }

    private List<OperatingHoursResult> resolveOperatingHours(
            List<OperatingHoursReadModel> operatingHours,
            OperatingContext context
    ) {
        return operatingHours.stream()
                .sorted(Comparator.comparing(OperatingHoursReadModel::period)
                        .thenComparing(OperatingHoursReadModel::dayGroup))
                .map(hours -> new OperatingHoursResult(
                        hours.period(),
                        hours.dayGroup(),
                        hours.status(),
                        hours.opensAt(),
                        hours.closesAt(),
                        hours.matches(context.period(), context.dayGroup())
                ))
                .toList();
    }

    private OperatingContext currentOperatingContext() {
        LocalDate today = LocalDate.now(clock);
        OperatingPeriod period = academicPeriodQueryPort.determineOperatingPeriod(today);
        OperatingDayGroup dayGroup = isWeekend(today) ? OperatingDayGroup.WEEKEND : OperatingDayGroup.WEEKDAY;

        return new OperatingContext(period, dayGroup);
    }

    private List<String> normalizeCategories(List<String> categories) {
        return categories.stream()
                .map(String::trim)
                .map(category -> category.toLowerCase(Locale.ROOT))
                .filter(category -> !category.isBlank())
                .distinct()
                .toList();
    }

    private String resolveImageUrl(String imagePath) {
        if (imagePath == null || imagePath.isBlank()) {
            return null;
        }
        return storagePort.getPresignedUrl(imagePath);
    }

}
