package com.kustacks.kuring.worker.update.staff.dto;

import com.kustacks.kuring.staff.domain.Staff;

import java.util.List;

public record StaffCompareResults(
        List<Staff> newStaffs,
        List<Staff> deleteStaffs
) {
}
