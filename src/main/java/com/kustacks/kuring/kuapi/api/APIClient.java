package com.kustacks.kuring.kuapi.api;

import com.kustacks.kuring.error.InternalLogicException;

import java.util.List;

public interface APIClient<T, K>{
    List<T> request(K k) throws InternalLogicException;
}
