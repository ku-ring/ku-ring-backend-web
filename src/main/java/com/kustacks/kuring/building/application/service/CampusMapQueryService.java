package com.kustacks.kuring.building.application.service;

import com.kustacks.kuring.building.application.port.in.CampusMapQueryUseCase;
import com.kustacks.kuring.building.application.port.in.dto.BuildingDetailResult;
import com.kustacks.kuring.building.application.port.in.dto.BuildingSummaryResult;
import com.kustacks.kuring.building.application.port.in.dto.CampusPlaceResult;
import com.kustacks.kuring.building.application.port.in.dto.CategoryResult;
import com.kustacks.kuring.building.application.port.in.dto.CurrentOperatingHoursResult;
import com.kustacks.kuring.building.application.port.out.AcademicPeriodPort;
import com.kustacks.kuring.building.application.port.out.CampusMapQueryPort;
import com.kustacks.kuring.building.domain.Building;
import com.kustacks.kuring.building.domain.CampusPlace;
import com.kustacks.kuring.building.domain.CampusPlaceCategory;
import com.kustacks.kuring.building.domain.OperatingDayGroup;
import com.kustacks.kuring.building.domain.OperatingHours;
import com.kustacks.kuring.building.domain.OperatingHoursStatus;
import com.kustacks.kuring.building.domain.OperatingPeriod;
import com.kustacks.kuring.common.annotation.UseCase;
import com.kustacks.kuring.storage.application.port.out.StoragePort;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

@UseCase
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CampusMapQueryService implements CampusMapQueryUseCase {

    private final CampusMapQueryPort campusMapQueryPort;
    private final AcademicPeriodPort academicPeriodPort;
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
    public Optional<BuildingDetailResult> getBuildingDetail(Long buildingId) {
        return campusMapQueryPort.findBuildingById(buildingId)
                .map(building -> toBuildingDetailResult(
                        building,
                        campusMapQueryPort.findCampusPlacesByBuildingId(buildingId),
                        currentOperatingContext()
                ));
    }

    private CategoryResult toCategoryResult(CampusPlaceCategory category) {
        return new CategoryResult(
                category.getCode(),
                category.getKorName(),
                category.getDisplayOrder()
        );
    }

    private BuildingSummaryResult toBuildingSummaryResult(Building building) {
        return new BuildingSummaryResult(
                building.getId(),
                building.getName(),
                building.getAddress(),
                building.getLat(),
                building.getLon()
        );
    }

    private CampusPlaceResult toCampusPlaceResult(
            CampusPlace place,
            OperatingContext context
    ) {
        return new CampusPlaceResult(
                place.getId(),
                place.getName(),
                place.getCategory().getCode(),
                place.getCategory().getKorName(),
                resolveImageUrl(place.getImagePath()),
                place.getLocationType(),
                place.getFloor(),
                place.getLocationDetail(),
                place.getQuantity(),
                resolveCurrentOperatingHours(place.getOperatingHours(), context),
                place.getExternalUrl(),
                toBuildingSummaryResult(place.getBuilding())
        );
    }

    private BuildingDetailResult toBuildingDetailResult(
            Building building,
            List<CampusPlace> campusPlaces,
            OperatingContext context
    ) {
        List<CampusPlaceResult> placeResults = campusPlaces.stream()
                .map(place -> toCampusPlaceResult(place, context))
                .toList();

        return new BuildingDetailResult(
                building.getId(),
                building.getName(),
                building.getAddress(),
                building.getLat(),
                building.getLon(),
                resolveImageUrl(building.getImagePath()),
                resolveCurrentOperatingHours(building.getOperatingHours(), context),
                placeResults
        );
    }

    private CurrentOperatingHoursResult resolveCurrentOperatingHours(
            Set<OperatingHours> operatingHours,
            OperatingContext context
    ) {
        Optional<OperatingHours> currentHours = findOperatingHours(
                operatingHours,
                context.period(),
                context.dayGroup()
        );

        return currentHours
                .map(hours -> new CurrentOperatingHoursResult(
                        context.period(),
                        context.dayGroup(),
                        hours.getStatus(),
                        hours.getOpensAt(),
                        hours.getClosesAt()
                ))
                .orElseGet(() -> new CurrentOperatingHoursResult(
                        context.period(),
                        context.dayGroup(),
                        OperatingHoursStatus.UNKNOWN,
                        null,
                        null
                ));
    }

    private Optional<OperatingHours> findOperatingHours(
            Set<OperatingHours> operatingHours,
            OperatingPeriod period,
            OperatingDayGroup dayGroup
    ) {
        return operatingHours.stream()
                .filter(hours -> hours.matches(period, dayGroup))
                .findFirst();
    }

    private OperatingContext currentOperatingContext() {
        LocalDate today = LocalDate.now(clock);
        OperatingPeriod period = academicPeriodPort.resolve(today);
        OperatingDayGroup dayGroup = isWeekend(today)
                ? OperatingDayGroup.WEEKEND
                : OperatingDayGroup.WEEKDAY;

        return new OperatingContext(period, dayGroup);
    }

    private List<String> normalizeCategories(List<String> categories) {
        return categories.stream()
                .flatMap(category -> Arrays.stream(category.split(",")))
                .map(String::trim)
                .map(category -> category.toLowerCase(Locale.ROOT))
                .filter(category -> !category.isBlank())
                .distinct()
                .toList();
    }

    private boolean isWeekend(LocalDate date) {
        return switch (date.getDayOfWeek()) {
            case SATURDAY, SUNDAY -> true;
            default -> false;
        };
    }

    private String resolveImageUrl(String imagePath) {
        if (imagePath == null || imagePath.isBlank()) {
            return null;
        }
        return storagePort.getPresignedUrl(imagePath);
    }

    private record OperatingContext(
            OperatingPeriod period,
            OperatingDayGroup dayGroup
    ) {
    }
}
