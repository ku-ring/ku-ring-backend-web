package com.kustacks.kuring.util.converter;

public interface DTOConverter<T, K> {
    T convert(K target);
}
