package com.kustacks.kuring.worker.update.staff;

import com.kustacks.kuring.staff.domain.Staff;
import com.kustacks.kuring.worker.update.staff.dto.StaffCompareResults;
import com.kustacks.kuring.worker.update.staff.dto.StaffDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class StaffCompareSupport {

    public StaffCompareResults compareAllDepartmentsAndUpdateExistStaff(
            List<StaffDto> scrapStaffStorage,
            Map<String, Staff> originStaffStorage
    ) {
        updateAlreadyExistsStaffs(scrapStaffStorage, originStaffStorage);
        List<Staff> newStaffs = filteringNewStaffs(scrapStaffStorage, originStaffStorage);
        List<Staff> deleteStaffs = filteringDeleteStaffs(scrapStaffStorage, originStaffStorage);
        return new StaffCompareResults(newStaffs, deleteStaffs);
    }

    private void updateAlreadyExistsStaffs(List<StaffDto> scrapStaffStorage, Map<String, Staff> originStaffStorage) {
        scrapStaffStorage.stream()
                .filter(staffDto -> originStaffStorage.containsKey(staffDto.getEmail()))
                .forEach(staffDto -> updateSingleStaff(staffDto, originStaffStorage));
    }

    private void updateSingleStaff(StaffDto staffDto, Map<String, Staff> originStaffStorage) {
        Staff staff = originStaffStorage.get(staffDto.getEmail());

        if (staffDto.isNotSameInformation(staff)) {
            updateStaffInfo(staffDto, staff);
        }
    }

    private List<Staff> filteringNewStaffs(List<StaffDto> scrapStaffStorage, Map<String, Staff> originStaffStorage) {
        return scrapStaffStorage.stream()
                .filter(staffDto -> !originStaffStorage.containsKey(staffDto.getEmail()))
                .map(StaffDto::toEntity)
                .toList();
    }

    private List<Staff> filteringDeleteStaffs(List<StaffDto> scrapStaffStorage, Map<String, Staff> originStaffStorage) {
        for (StaffDto staffDto : scrapStaffStorage) {
            originStaffStorage.remove(staffDto.getEmail());
        }
        return originStaffStorage.values().stream().toList();
    }

    private static void updateStaffInfo(StaffDto staffDto, Staff staff) {
        staff.updateInformation(
                staffDto.getName(),
                staffDto.getMajor(),
                staffDto.getLab(),
                staffDto.getPhone(),
                staffDto.getEmail(),
                staffDto.getDeptName(),
                staffDto.getCollegeName(),
                staffDto.getPosition()
        );
    }
}
