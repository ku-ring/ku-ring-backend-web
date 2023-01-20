package com.kustacks.kuring.service;

import com.kustacks.kuring.staff.domain.Staff;

import java.util.List;

public interface StaffService {
    List<Staff> handleSearchRequest(String keywords);
}
