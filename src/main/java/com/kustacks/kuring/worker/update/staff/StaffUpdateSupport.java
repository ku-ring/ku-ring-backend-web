package com.kustacks.kuring.worker.update.staff;

import com.kustacks.kuring.staff.domain.Staff;
import com.kustacks.kuring.worker.update.staff.dto.StaffCompareResults;
import com.kustacks.kuring.worker.update.staff.dto.StaffDto;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component
public class StaffUpdateSupport {

    public StaffCompareResults compareAllDepartments(
            List<StaffDto> scrapStaffStorage,
            Map<String, Staff> originStaffStorage
    ) {
        List<Staff> newStaffs = compareAllStaffs(scrapStaffStorage, originStaffStorage);
        List<Staff> deleteStaffs = convertDeleteStaffs(originStaffStorage);
        return new StaffCompareResults(newStaffs, deleteStaffs);
    }

    private List<Staff> compareAllStaffs(List<StaffDto> scrapStaffStorage, Map<String, Staff> originStaffStorage) {
        return scrapStaffStorage
                .stream()
                .map(staffDto -> compareSingleStaff(staffDto, originStaffStorage))
                .flatMap(List::stream)
                .toList();
    }

    private List<Staff> compareSingleStaff(StaffDto staffDto, Map<String, Staff> originStaffStorage) {
        List<Staff> newStaffs = new LinkedList<>();

        if (originStaffStorage.containsKey(staffDto.getEmail())) {
            Staff staff = originStaffStorage.get(staffDto.getEmail());

            if (staffDto.isNotSameInformation(staff)) {
                staff.updateInformation(
                        staffDto.getName(),
                        staffDto.getMajor(),
                        staffDto.getLab(),
                        staffDto.getPhone(),
                        staffDto.getEmail(),
                        staffDto.getDeptName(),
                        staffDto.getCollegeName()
                );
            }

            originStaffStorage.remove(staffDto.getEmail()); //  최종적으로 originStaffStorage에는 삭제할 대상만 남게 된다.
        } else {
            newStaffs.add(staffDto.toEntity());
        }

        return newStaffs;
    }

    private static List<Staff> convertDeleteStaffs(Map<String, Staff> originStaffStorage) {
        return originStaffStorage.values().stream().toList();
    }
}
