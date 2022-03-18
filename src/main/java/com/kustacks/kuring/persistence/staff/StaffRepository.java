package com.kustacks.kuring.persistence.staff;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {

    List<Staff> findByNameContainingOrDeptContainingOrCollegeContaining(String name, String dept, String college);
}
