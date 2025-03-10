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
        int limit = 3;
        CursorGenerator<Integer> cursorGenerator = (content) -> content.toString();

        List<Integer> sourceContents = List.of(1, 2, 3, 4, 5);
        Function<Integer, List<Integer>> sourceContentsLoader = (size) -> subListWithSize(sourceContents, 0, size);

        // When
        CursorBasedList<Integer> cursorBasedList = CursorBasedList.of(limit, cursorGenerator, sourceContentsLoader);

        // Then
        assertAll(
                () -> assertThat(cursorBasedList.getContents()).isEqualTo(List.of(1, 2, 3)),
                () -> assertThat(cursorBasedList.getEndCursor()).isEqualTo("3"),
                () -> assertThat(cursorBasedList.hasNext()).isTrue()
        );
    }

    @DisplayName("of()는 sourceContents가 limit보다 작은 경우, hasNext가 false인 CursorBasedList를 반환해야 한다.")
    @Test
    void of_shouldReturnCursorBasedListWithHasNextAsFalseWhenSourceContentsSmallerThanLimit() {
        // Given
        int limit = 3;
        CursorGenerator<Integer> cursorGenerator = (content) -> content.toString();

        List<Integer> sourceContents = List.of(1, 2);
        Function<Integer, List<Integer>> sourceContentsLoader = (size) -> subListWithSize(sourceContents, 0, size);

        // When
        CursorBasedList<Integer> cursorBasedList = CursorBasedList.of(limit, cursorGenerator, sourceContentsLoader);

        // Then
        assertAll(
                () -> assertThat(cursorBasedList.getContents()).isEqualTo(sourceContents),
                () -> assertThat(cursorBasedList.getEndCursor()).isNull(),
                () -> assertThat(cursorBasedList.hasNext()).isFalse()
        );
    }

    @DisplayName("of()는 limit이 sourceContents의 크기와 같을 때, hasNext가 false인 CursorBasedList를 반환해야 한다.")
    @Test
    void of_shouldReturnCursorBasedListWithHasNextAsFalseWhenLimitIsEqualToSourceContentsSize() {
        // Given
        List<Integer> sourceContents = List.of(1, 2, 3);
        int limit = 3;

        CursorGenerator<Integer> cursorGenerator = (content) -> content.toString();
        Function<Integer, List<Integer>> sourceContentsLoader = (size) -> subListWithSize(sourceContents, 0, size);

        // When
        CursorBasedList<Integer> cursorBasedList = CursorBasedList.of(limit, cursorGenerator, sourceContentsLoader);

        // Then
        assertAll(
                () -> assertThat(cursorBasedList.getContents()).isEqualTo(sourceContents),
                () -> assertThat(cursorBasedList.getEndCursor()).isNull(),
                () -> assertThat(cursorBasedList.hasNext()).isFalse()
        );
    }

    @DisplayName("of()는 limit이 sourceContents보다 작은 경우, hasNext가 true인 CursorBasedList를 반환해야 한다.")
    @Test
    void of_shouldReturnCursorBasedListWithHasNextAsTrueWhenLimitIsSmallerThanSourceContents() {
        // Given
        int limit = 3;
        CursorGenerator<Integer> cursorGenerator = (content) -> content.toString();

        List<Integer> sourceContents = List.of(1, 2, 3, 4, 5);
        Function<Integer, List<Integer>> sourceContentsLoader = (size) -> subListWithSize(sourceContents, 0, size);

        // When
        CursorBasedList<Integer> cursorBasedList = CursorBasedList.of(limit, cursorGenerator, sourceContentsLoader);

        // Then
        assertAll(
                () -> assertThat(cursorBasedList.getContents()).isEqualTo(List.of(1, 2, 3)),
                () -> assertThat(cursorBasedList.getEndCursor()).isEqualTo("3"),
                () -> assertThat(cursorBasedList.hasNext()).isTrue()
        );
    }

    public static <T> List<T> subListWithSize(List<T> list, int start, int size) {
        if (list == null) {
            throw new IllegalArgumentException("List cannot be null");
        }
        if (start < 0 || size < 0 || start >= list.size()) {
            throw new IndexOutOfBoundsException("Invalid start index or size");
        }

        int end = Math.min(start + size, list.size());
        return list.subList(start, end);
    }
}

