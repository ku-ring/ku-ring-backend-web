package com.kustacks.kuring.service;

import com.kustacks.kuring.persistence.staff.Staff;

import java.util.List;

public interface StaffService {
    List<Staff> handleSearchRequest(String keywords);
}
