package com.kustacks.kuring.staff.adapter.out.persistence;

import com.kustacks.kuring.staff.domain.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StaffRepository extends JpaRepository<Staff, Long>, StaffQueryRepository {

    List<Staff> findByDeptContaining(String deptName);
}
