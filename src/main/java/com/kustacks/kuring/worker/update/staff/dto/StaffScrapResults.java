package com.kustacks.kuring.worker.update.staff.dto;

import java.util.List;
import java.util.Map;

public record StaffScrapResults(
        Map<String, StaffDto> kuStaffDTOMap
) {

}
