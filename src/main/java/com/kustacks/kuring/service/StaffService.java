package com.kustacks.kuring.service;

import com.kustacks.kuring.domain.staff.Staff;

import java.util.List;

public interface StaffService {
    List<Staff> handleSearchRequest(String keywords);
}
