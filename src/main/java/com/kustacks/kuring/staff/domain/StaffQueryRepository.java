package com.kustacks.kuring.staff.domain;

import com.kustacks.kuring.staff.common.dto.response.StaffSearchDto;

import java.util.List;

public interface StaffQueryRepository {

    List<StaffSearchDto> findAllByKeywords(List<String> keywords);
}
