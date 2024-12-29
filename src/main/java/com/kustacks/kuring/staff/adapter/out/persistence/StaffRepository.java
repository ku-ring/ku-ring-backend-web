package com.kustacks.kuring.staff.adapter.out.persistence;

import com.kustacks.kuring.staff.domain.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffRepository extends JpaRepository<Staff, Long>, StaffQueryRepository {

}
