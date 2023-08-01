package com.kustacks.kuring.worker.scrap.client.notice;

import com.kustacks.kuring.common.exception.InternalLogicException;

import java.util.List;

public interface NoticeApiClient<T, P> {

    List<T> request(P p) throws InternalLogicException;

    List<T> requestAll(P p) throws InternalLogicException;
}
