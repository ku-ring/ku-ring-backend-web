package com.kustacks.kuring.staff.application.port.in;

import com.kustacks.kuring.staff.application.port.in.dto.StaffSearchResult;

import java.util.List;

public interface StaffQueryUseCase {
    List<StaffSearchResult> findAllStaffByContent(String content);
}
