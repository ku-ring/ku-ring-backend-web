package com.kustacks.kuring.common.utils.converter;

public interface Converter<T, K> {
    T convert(K k);
}
