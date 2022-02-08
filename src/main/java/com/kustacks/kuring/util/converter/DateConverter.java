package com.kustacks.kuring.util.converter;

import java.text.ParseException;

public interface DateConverter<T, K> {
    T convert(K k);
}
