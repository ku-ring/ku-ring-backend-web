package com.kustacks.kuring.staff.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface StaffRepository extends JpaRepository<Staff, Long> {
    default Map<String, Staff> findAllMap() {
        return findAll().stream().collect(Collectors.toMap(Staff::getEmail, v -> v));
    }

    default Map<String, Staff> findByDeptContainingMap(List<String> deptNames) {

        Map<String, Staff> staffMap = new HashMap<>();
        for (String deptName : deptNames) {
            List<Staff> staffs = findByDeptContaining(deptName);
            for (Staff staff : staffs) {
                Staff staffInMap = staffMap.get(staff.getEmail());
                if (staffInMap == null) {
                    staffMap.put(staff.getEmail(), staff);
                }
            }
        }

        return staffMap;
    }

    List<Staff> findByDeptContaining(String deptName);

    List<Staff> findByNameContainingOrDeptContainingOrCollegeContaining(String name, String dept, String college);
}
