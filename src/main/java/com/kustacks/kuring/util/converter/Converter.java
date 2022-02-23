package com.kustacks.kuring.util.converter;

public interface Converter<T, K> {
    T convert(K k);
}
