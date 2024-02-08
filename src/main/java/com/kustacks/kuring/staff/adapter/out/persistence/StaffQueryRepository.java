package com.kustacks.kuring.staff.adapter.out.persistence;

import com.kustacks.kuring.staff.application.port.out.dto.StaffSearchDto;

import java.util.List;

public interface StaffQueryRepository {

    List<StaffSearchDto> findAllByKeywords(List<String> keywords);
}
