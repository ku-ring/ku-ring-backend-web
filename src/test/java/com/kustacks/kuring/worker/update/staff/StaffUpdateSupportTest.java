package com.kustacks.kuring.worker.update.staff;

import com.kustacks.kuring.staff.domain.Staff;
import com.kustacks.kuring.worker.update.staff.dto.StaffCompareResults;
import com.kustacks.kuring.worker.update.staff.dto.StaffDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class StaffUpdateSupportTest {

    private StaffUpdateSupport updateSupport;

    @BeforeEach
    void setUp() {
        updateSupport = new StaffUpdateSupport();
    }

    @DisplayName("이미 저장되어 있던 교직원 정보와 신규 정보를 비교하고 업데이트 한다")
    @Test
    void compareAllDepartments() {
        // given
        Map<String, Staff> originStaffMap = new HashMap<>();
        Staff deleteStaff = Staff.builder()
                .name("김길동")
                .major("분자생물학")
                .lab("동물생명과학관")
                .phone("010-5678-1234")
                .email("life@test.com")
                .dept("생명과학부")
                .college("상허생명과학대학")
                .build();

        Staff updateStaff = Staff.builder()
                .name("홍길동")
                .major("AI")
                .lab("공과대 A동")
                .phone("010-1234-5678")
                .email("email@test.com")
                .dept("컴퓨터공학과")
                .college("공과대학")
                .build();

        originStaffMap.put(updateStaff.getEmail(), updateStaff);
        originStaffMap.put(deleteStaff.getEmail(), deleteStaff);

        StaffDto updateStaffDto = StaffDto.builder()
                .name("홍길동")
                .major("AI")
                .lab("공과대 A동")
                .phone("010-1234-5678")
                .email("email@test.com")
                .deptName("컴퓨터공학부, 스마트ICT융합공학과")
                .collegeName("공과대학")
                .build();

        StaffDto newStaffDto1 = StaffDto.builder()
                .name("고길동")
                .major("발생생물학")
                .lab("동물생명과학관")
                .phone("010-5678-5678")
                .email("brain@test.com")
                .deptName("생명과학부")
                .collegeName("상허생명과학대학")
                .build();

        StaffDto newStaffDto2 = StaffDto.builder()
                .name("김샤인")
                .major("분자생물학")
                .lab("동물생명과학관")
                .phone("010-1111-2222")
                .email("molecular@test.com")
                .deptName("생명과학부")
                .collegeName("상허생명과학대학")
                .build();

        List<StaffDto> staffDtos = List.of(updateStaffDto, newStaffDto1, newStaffDto2);

        // when
        StaffCompareResults results = updateSupport.compareAllDepartments(staffDtos, originStaffMap);

        // then
        assertAll(
                () -> assertThat(newStaffDto1.isNotSameInformation(results.newStaffs().get(0))).isFalse(),
                () -> assertThat(newStaffDto2.isNotSameInformation(results.newStaffs().get(1))).isFalse(),
                () -> assertThat(deleteStaff).isEqualTo(results.deleteStaffs().get(0)),
                () -> assertThat(updateStaff.getDept()).isEqualTo("컴퓨터공학부, 스마트ICT융합공학과")
        );
    }
}
