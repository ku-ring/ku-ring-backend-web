package com.kustacks.kuring.staff.adapter.out.persistence;

import com.kustacks.kuring.staff.common.dto.QStaffSearchDto;
import com.kustacks.kuring.staff.application.port.out.dto.StaffSearchDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.kustacks.kuring.staff.domain.QStaff.staff;

@RequiredArgsConstructor
public class StaffQueryRepositoryImpl implements StaffQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<StaffSearchDto> findAllByKeywords(List<String> keywords) {
        return queryFactory
                .select(new QStaffSearchDto(
                        staff.name,
                        staff.major,
                        staff.lab,
                        staff.phone,
                        staff.email,
                        staff.dept,
                        staff.college
                ))
                .from(staff)
                .where(isContainName(keywords).or(isContainDept(keywords)).or(isContainCollege(keywords)))
                .fetch();
    }

    private static BooleanBuilder isContainName(List<String> keywords) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        for (String keyword : keywords) {
            booleanBuilder.or(staff.name.contains(keyword));
        }

        return booleanBuilder;
    }

    private static BooleanBuilder isContainDept(List<String> keywords) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        for (String keyword : keywords) {
            booleanBuilder.or(staff.dept.contains(keyword));
        }

        return booleanBuilder;
    }

    private static BooleanBuilder isContainCollege(List<String> keywords) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        for (String keyword : keywords) {
            booleanBuilder.or(staff.college.contains(keyword));
        }

        return booleanBuilder;
    }
}
