package com.kustacks.kuring.common.data;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CursorBasedListTest {

    @DisplayName("비어있는 데이터의 경우 Empty CursorBasedList를 반환한다")
    @Test
    void empty_shouldReturnAnEmptyCursorBasedList() {
        // Given
        CursorBasedList<Integer> emptyList = CursorBasedList.empty();

        // Then
        assertTrue(emptyList.getContents().isEmpty());
        assertNull(emptyList.getEndCursor());
        assertFalse(emptyList.hasNext());
    }

    @DisplayName("of()는 정확한 contents, endCursor, hasNext 값을 가진 CursorBasedList를 반환해야 한다.")
    @Test
    void of_shouldReturnCursorBasedListWithCorrectContentsEndCursorAndHasNext() {
        // Given
        List<Integer> sourceContents = List.of(1, 2, 3, 4, 5);
        int limit = 3;
        Function<Integer, String> cursorGenerator = (content) -> content.toString();

        // When
        CursorBasedList<Integer> cursorBasedList = CursorBasedList.baseOf(sourceContents, limit, cursorGenerator);

        // Then
        assertAll(
                () -> assertThat(cursorBasedList.getContents()).isEqualTo(List.of(1, 2, 3)),
                () -> assertThat(cursorBasedList.getEndCursor()).isEqualTo("3"),
                () -> assertThat(cursorBasedList.hasNext()).isTrue()
        );
    }

    @DisplayName("limit이 sourceContents의 크기보다 클 때, hasNext가 false인 CursorBasedList를 반환해야 한다.")
    @Test
    void of_shouldReturnCursorBasedListWithHasNextAsFalseWhenLimitIsGreaterThanSourceContentsSize() {
        // Given
        List<Integer> sourceContents = List.of(1, 2, 3);
        int limit = 5;
        Function<Integer, String> cursorGenerator = (content) -> content.toString();

        // When
        CursorBasedList<Integer> cursorBasedList = CursorBasedList.baseOf(sourceContents, limit, cursorGenerator);

        // Then
        assertAll(
                () -> assertThat(cursorBasedList.getContents()).isEqualTo(List.of(1, 2, 3)),
                () -> assertThat(cursorBasedList.getEndCursor()).isNull(),
                () -> assertThat(cursorBasedList.hasNext()).isFalse()
        );
    }

    @DisplayName("limit이 sourceContents의 크기와 같을 때, hasNext가 false인 CursorBasedList를 반환해야 한다.")
    @Test
    void of_shouldReturnCursorBasedListWithHasNextAsFalseWhenLimitIsEqualToSourceContentsSize() {
        // Given
        List<Integer> sourceContents = List.of(1, 2, 3, 4, 5);
        int limit = 5;
        Function<Integer, String> cursorGenerator = (content) -> content.toString();

        // When
        CursorBasedList<Integer> cursorBasedList = CursorBasedList.baseOf(sourceContents, limit, cursorGenerator);

        // Then
        assertAll(
                () -> assertThat(cursorBasedList.getContents()).isEqualTo(List.of(1, 2, 3, 4, 5)),
                () -> assertThat(cursorBasedList.getEndCursor()).isNull(),
                () -> assertThat(cursorBasedList.hasNext()).isFalse()
        );
    }

    @DisplayName("baseOf()는 정확한 contents, endCursor, hasNext 값을 가진 CursorBasedList를 반환해야 한다.")
    @Test
    void baseOf_shouldReturnCursorBasedListWithCorrectContentsEndCursorAndHasNext() {
        // Given
        int limit = 3;
        Function<Integer, String> cursorGenerator = (content) -> content.toString();

        List<Integer> sourceContents = List.of(1, 2, 3, 4, 5);
        Function<Integer, List<Integer>> sourceContentsLoader = (size) -> sourceContents.subList(0, size);

        // When
        CursorBasedList<Integer> cursorBasedList = CursorBasedList.baseOf(limit, cursorGenerator, sourceContentsLoader);

        // Then
        assertAll(
                () -> assertThat(cursorBasedList.getContents()).isEqualTo(List.of(1, 2, 3)),
                () -> assertThat(cursorBasedList.getEndCursor()).isEqualTo("3"),
                () -> assertThat(cursorBasedList.hasNext()).isTrue()
        );
    }

    @DisplayName("baseOf()는 sourceContents가 limit보다 작은 경우, hasNext가 false인 CursorBasedList를 반환해야 한다.")
    @Test
    void baseOf_shouldReturnCursorBasedListWithHasNextAsFalseWhenSourceContentsSmallerThanLimit() {
        // Given
        int limit = 3;
        Function<Integer, String> cursorGenerator = (content) -> content.toString();

        List<Integer> sourceContents = List.of(1, 2);
        Function<Integer, List<Integer>> sourceContentsLoader = (size) -> sourceContents.subList(0, Math.min(size, sourceContents.size()));

        // When
        CursorBasedList<Integer> cursorBasedList = CursorBasedList.baseOf(limit, cursorGenerator, sourceContentsLoader);

        // Then
        assertAll(
                () -> assertThat(cursorBasedList.getContents()).isEqualTo(sourceContents),
                () -> assertThat(cursorBasedList.getEndCursor()).isNull(),
                () -> assertThat(cursorBasedList.hasNext()).isFalse()
        );
    }

    @DisplayName("baseOf()는 limit이 sourceContents의 크기와 같을 때, hasNext가 false인 CursorBasedList를 반환해야 한다.")
    @Test
    void baseOf_shouldReturnCursorBasedListWithHasNextAsFalseWhenLimitIsEqualToSourceContentsSize() {
        // Given
        List<Integer> sourceContents = List.of(1, 2, 3);
        int limit = 3;

        Function<Integer, String> cursorGenerator = (content) -> content.toString();
        Function<Integer, List<Integer>> sourceContentsLoader = (size) -> sourceContents.subList(0, Math.min(size, sourceContents.size()));

        // When
        CursorBasedList<Integer> cursorBasedList = CursorBasedList.baseOf(limit, cursorGenerator, sourceContentsLoader);

        // Then
        assertAll(
                () -> assertThat(cursorBasedList.getContents()).isEqualTo(sourceContents),
                () -> assertThat(cursorBasedList.getEndCursor()).isNull(),
                () -> assertThat(cursorBasedList.hasNext()).isFalse()
        );
    }

    @DisplayName("baseOf()는 limit이 sourceContents보다 작은 경우, hasNext가 true인 CursorBasedList를 반환해야 한다.")
    @Test
    void baseOf_shouldReturnCursorBasedListWithHasNextAsTrueWhenLimitIsSmallerThanSourceContents() {
        // Given
        int limit = 3;
        Function<Integer, String> cursorGenerator = (content) -> content.toString();

        List<Integer> sourceContents = List.of(1, 2, 3, 4, 5);
        Function<Integer, List<Integer>> sourceContentsLoader = (size) -> sourceContents.subList(0, size);

        // When
        CursorBasedList<Integer> cursorBasedList = CursorBasedList.baseOf(limit, cursorGenerator, sourceContentsLoader);

        // Then
        assertAll(
                () -> assertThat(cursorBasedList.getContents()).isEqualTo(List.of(1, 2, 3)),
                () -> assertThat(cursorBasedList.getEndCursor()).isEqualTo("3"),
                () -> assertThat(cursorBasedList.hasNext()).isTrue()
        );
    }
}

