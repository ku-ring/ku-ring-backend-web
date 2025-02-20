package com.kustacks.kuring.common.data;

@FunctionalInterface
public interface CursorGenerator<T> {
    String generate(T content);
}
