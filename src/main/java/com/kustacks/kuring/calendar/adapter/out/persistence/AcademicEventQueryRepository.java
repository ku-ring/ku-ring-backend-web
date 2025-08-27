package com.kustacks.kuring.calendar.adapter.out.persistence;

import java.util.List;

public interface AcademicEventQueryRepository {

    void deleteAll(List<Long> oldEventIds);
}
