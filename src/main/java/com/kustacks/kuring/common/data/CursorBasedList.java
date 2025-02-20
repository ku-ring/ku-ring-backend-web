package com.kustacks.kuring.common.data;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Function;

public class CursorBasedList<T> implements List<T> {

    private final List<T> contents;
    private final String endCursor;
    private final boolean hasNext;

    private static final int NEXT_CURSOR_SIZE = 1;

    public CursorBasedList(List<T> contents, String endCursor, boolean hasNext) {
        this.contents = contents;
        this.endCursor = endCursor;
        this.hasNext = hasNext;
    }

    public List<T> getContents() {
        return contents;
    }

    public String getEndCursor() {
        return endCursor;
    }

    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public int size() {
        return contents.size();
    }

    @Override
    public boolean isEmpty() {
        return contents.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return contents.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return contents.listIterator();
    }

    @Override
    public Object[] toArray() {
        return contents.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return contents.toArray(a);
    }

    @Override
    public boolean add(T t) {
        return contents.add(t);
    }

    @Override
    public boolean remove(Object o) {
        return contents.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return false;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {
        contents.clear();
    }

    @Override
    public T get(int index) {
        return contents.get(index);
    }

    @Override
    public T set(int index, T element) {
        return contents.set(index, element);
    }

    @Override
    public void add(int index, T element) {
        contents.add(index, element);
    }

    @Override
    public T remove(int index) {
        return contents.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return contents.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return contents.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return contents.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return contents.listIterator(index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return contents.subList(fromIndex, toIndex);
    }

    public static <T> CursorBasedList<T> empty() {
        return new CursorBasedList<>(List.of(), null, false);
    }

    public static <T> CursorBasedList<T> baseOf(
            List<T> sourceContents,
            int limit,
            Function<T, String> cursorGenerator
    ) {
        List<T> contents = sourceContents.subList(0, Math.min(limit, sourceContents.size()));
        boolean hasNext = limit < sourceContents.size();
        String endCursor = hasNext ? cursorGenerator.apply(contents.get(limit - 1)) : null;

        return new CursorBasedList<>(contents, endCursor, hasNext);
    }

    public static <T> CursorBasedList<T> baseOf(
            int limit,
            Function<T, String> cursorGenerator,
            Function<Integer, List<T>> sourceContentsLoader
    ) {
        List<T> sourceContents = sourceContentsLoader.apply(limit + NEXT_CURSOR_SIZE);

        int subListSize = Math.min(limit, sourceContents.size());
        List<T> contents = sourceContents.subList(0, subListSize);

        boolean hasNext = limit < sourceContents.size();
        String endCursor = hasNext ? cursorGenerator.apply(contents.get(limit - 1)) : null;

        return new CursorBasedList<>(contents, endCursor, hasNext);
    }
}
