package com.kustacks.kuring.worker.scrap.client.notice;

import com.kustacks.kuring.common.error.InternalLogicException;

import java.util.List;

// TODO: support(DepartmentName) 필요
public interface NoticeApiClient<T, P> {

    List<T> request(P p) throws InternalLogicException;

    List<T> requestAll(P p) throws InternalLogicException;
}
