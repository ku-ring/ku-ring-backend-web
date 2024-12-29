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
            Map<String, StaffDto> scrapStaffStorage,
            Map<String, Staff> originStaffStorage
    ) {
        updateAlreadyExistsStaffs(scrapStaffStorage, originStaffStorage);
        List<Staff> newStaffs = filteringNewStaffs(scrapStaffStorage, originStaffStorage);
        List<Staff> deleteStaffs = filteringDeleteStaffs(scrapStaffStorage, originStaffStorage);
        return new StaffCompareResults(newStaffs, deleteStaffs);
    }

    private void updateAlreadyExistsStaffs(Map<String, StaffDto> scrapStaffStorage, Map<String, Staff> originStaffStorage) {
        scrapStaffStorage.keySet().stream()
                .filter(originStaffStorage::containsKey)
                .forEach(staffDtoKey -> updateSingleStaff(scrapStaffStorage.get(staffDtoKey), originStaffStorage));
    }

    private void updateSingleStaff(StaffDto staffDto, Map<String, Staff> originStaffStorage) {
        Staff staff = originStaffStorage.get(staffDto.identifier());

        if (staffDto.isNotSameInformation(staff)) {
            updateStaffInfo(staffDto, staff);
        }
    }

    private List<Staff> filteringNewStaffs(Map<String, StaffDto> scrapStaffStorage, Map<String, Staff> originStaffStorage) {
        return scrapStaffStorage.keySet().stream()
                .filter(staffDtoKey -> !originStaffStorage.containsKey(staffDtoKey))
                .map(staffDtoKey -> scrapStaffStorage.get(staffDtoKey).toEntity())
                .toList();
    }

    private List<Staff> filteringDeleteStaffs(Map<String, StaffDto> scrapStaffStorage, Map<String, Staff> originStaffStorage) {
        for (String staffDtoKey : scrapStaffStorage.keySet()) {
            originStaffStorage.remove(staffDtoKey);
        }
        return originStaffStorage.values().stream().toList();
    }

    private void updateStaffInfo(StaffDto staffDto, Staff staff) {
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
