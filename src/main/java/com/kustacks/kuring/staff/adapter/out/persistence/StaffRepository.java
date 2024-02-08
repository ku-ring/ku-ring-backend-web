package com.kustacks.kuring.staff.adapter.out.persistence;

import com.kustacks.kuring.staff.domain.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface StaffRepository extends JpaRepository<Staff, Long>, StaffQueryRepository {

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
}
