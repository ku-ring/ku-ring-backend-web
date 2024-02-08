package com.kustacks.kuring.staff.application.port.out;

import com.kustacks.kuring.staff.application.port.out.dto.StaffSearchDto;

import java.util.List;

public interface StaffQueryPort {
    List<StaffSearchDto> findAllByKeywords(List<String> keywords);
}
