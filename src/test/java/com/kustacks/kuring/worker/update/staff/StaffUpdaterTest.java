package com.kustacks.kuring.worker.update.staff;

import com.kustacks.kuring.staff.adapter.out.persistence.StaffRepository;
import com.kustacks.kuring.staff.domain.Staff;
import com.kustacks.kuring.support.IntegrationTestSupport;
import com.kustacks.kuring.worker.update.staff.dto.StaffDto;
import com.kustacks.kuring.worker.update.staff.dto.StaffScrapResults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class StaffUpdaterTest extends IntegrationTestSupport {

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private StaffDataSynchronizer staffDataSynchronizer;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        staffRepository.deleteAll();
    }

    @DisplayName("이미 저장되어 있던 교직원 정보와 신규 정보를 비교하고 업데이트 한다")
    @Test
    void compareAndUpdateDb() {
        // given
        Staff staff1 = Staff.builder()
                .name("홍길동")
                .major("AI")
                .lab("공과대 A동")
                .phone("02-1234-5678")
                .email("email@test.com")
                .dept("컴퓨터공학과")
                .college("공과대학")
                .position("조교수")
                .build();

        Staff staff2 = Staff.builder()
                .name("김길동")
                .major("분자생물학")
                .lab("동물생명과학관")
                .phone("02-5678-1234")
                .email("life@test.com")
                .dept("생명과학부")
                .college("상허생명과학대학")
                .position("교수")
                .build();

        staffRepository.saveAll(List.of(staff1, staff2));

        Map<String, StaffDto> kuStaffDtoMap = new HashMap<>();
        StaffDto dto1 = StaffDto.builder()
                .name("홍길동")
                .major("AI")
                .lab("공과대 A동")
                .phone("02-1234-5678")
                .email("email@test.com")
                .deptName("컴퓨터공학부, 스마트ICT융합공학과")
                .collegeName("공과대학")
                .position("조교수")
                .build();

        StaffDto dto2 = StaffDto.builder()
                .name("고길동")
                .major("발생생물학")
                .lab("동물생명과학관")
                .phone("02-5678-5678")
                .email("brain@test.com")
                .deptName("생명과학부")
                .collegeName("상허생명과학대학")
                .position("명예교수")
                .build();

        kuStaffDtoMap.put(dto1.identifier(), dto1);
        kuStaffDtoMap.put(dto2.identifier(), dto2);

        List<String> successDepartmentNames = List.of("컴퓨터공학과", "생명과학부");
        StaffScrapResults staffScrapResults = new StaffScrapResults(kuStaffDtoMap, successDepartmentNames);

        // when
        staffDataSynchronizer.compareAndUpdateDb(staffScrapResults);

        // then
        List<Staff> staffList = staffRepository.findAll();
        assertAll(
                () -> assertThat(staffList).hasSize(2),
                () -> assertThat(staffList).extracting("name").contains("홍길동", "고길동"),
                () -> assertThat(staffList).extracting("major").contains("AI", "발생생물학"),
                () -> assertThat(staffList).extracting("lab").contains("공과대 A동", "동물생명과학관"),
                () -> assertThat(staffList).extracting("phone").contains("02-1234-5678", "02-5678-5678"),
                () -> assertThat(staffList).extracting("dept").contains("컴퓨터공학부, 스마트ICT융합공학과", "생명과학부"),
                () -> assertThat(staffList).extracting("position").contains("조교수", "명예교수")
        );
    }

}
