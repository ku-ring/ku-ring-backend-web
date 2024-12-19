package com.kustacks.kuring.worker.update.staff;

import com.kustacks.kuring.staff.adapter.out.persistence.StaffRepository;
import com.kustacks.kuring.staff.domain.Staff;
import com.kustacks.kuring.worker.update.staff.dto.StaffCompareResults;
import com.kustacks.kuring.worker.update.staff.dto.StaffScrapResults;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class StaffDataSynchronizer {

    private final StaffCompareSupport staffCompareSupport;
    private final StaffRepository staffRepository;

    @Transactional
    public void compareAndUpdateDb(StaffScrapResults scrapResults) {
        StaffCompareResults compareResults = compare(scrapResults);
        synchronizationWithDb(compareResults);
    }

    private StaffCompareResults compare(StaffScrapResults scrapResults) {
        Map<String, Staff> originStaffStorage = findAllOriginStaffs();
        return staffCompareSupport.compareAllDepartmentsAndUpdateExistStaff(scrapResults.kuStaffDTOMap(), originStaffStorage);
    }

    private void synchronizationWithDb(StaffCompareResults compareResults) {
        staffRepository.deleteAll(compareResults.deleteStaffs());
        staffRepository.saveAll(compareResults.newStaffs());
    }

    private Map<String, Staff> findAllOriginStaffs() {
        return staffRepository.findAll().stream()
                .collect(
                        Collectors.toMap(
                                Staff::identifier,
                                Function.identity(),
                                (existing, newValue) -> existing
                        )
                );
    }
}
