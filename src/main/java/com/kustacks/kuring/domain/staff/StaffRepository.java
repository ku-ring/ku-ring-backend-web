package com.kustacks.kuring.domain.staff;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {
    default Map<String, Staff> findAllMap() {
        return findAll().stream().collect(Collectors.toMap(Staff::getEmail, v -> v));
    }
    List<Staff> findByNameContainingOrDeptContainingOrCollegeContaining(String name, String dept, String college);
}
