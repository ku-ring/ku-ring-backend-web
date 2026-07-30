package com.kustacks.kuring.building.adapter.out.persistence;

import com.kustacks.kuring.building.domain.CampusPlaceCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("영속성 어댑터 : CampusMapPersistenceAdapter")
@ExtendWith(MockitoExtension.class)
class CampusMapPersistenceAdapterTest {

    @Mock
    private CampusPlaceCategoryRepository categoryRepository;

    @InjectMocks
    private CampusMapPersistenceAdapter campusMapPersistenceAdapter;

    @Test
    @DisplayName("필터에 노출되는 카테고리를 노출 순서대로 조회한다")
    void find_filter_categories() {
        // given
        List<CampusPlaceCategory> categories = List.of(
                new CampusPlaceCategory("cafe", "카페", 1, true),
                new CampusPlaceCategory("restaurant", "식당", 2, true)
        );
        when(categoryRepository.findByFilterEnabledTrueOrderByDisplayOrderAscIdAsc())
                .thenReturn(categories);

        // when
        List<CampusPlaceCategory> result = campusMapPersistenceAdapter.findFilterCategories();

        // then
        assertThat(result).containsExactlyElementsOf(categories);
        verify(categoryRepository).findByFilterEnabledTrueOrderByDisplayOrderAscIdAsc();
    }
}
