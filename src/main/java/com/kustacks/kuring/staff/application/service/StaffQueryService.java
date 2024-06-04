package com.kustacks.kuring.staff.application.service;

import com.kustacks.kuring.staff.application.port.in.StaffQueryUseCase;
import com.kustacks.kuring.staff.application.port.in.dto.StaffSearchResult;
import com.kustacks.kuring.staff.application.port.out.StaffQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StaffQueryService implements StaffQueryUseCase {

    private static final String SPACE_REGEX = "[\\s+]";
    private final StaffQueryPort staffQueryPort;

    public List<StaffSearchResult> findAllStaffByContent(String content) {
        List<String> splitedKeywords = Arrays.asList(splitBySpace(content));
        return searchStaffByKeywords(splitedKeywords);
    }

    private List<StaffSearchResult> searchStaffByKeywords(List<String> splitedKeywords) {
        return staffQueryPort.findAllByKeywords(splitedKeywords)
                .stream()
                .map(dto -> new StaffSearchResult(
                        dto.getName(),
                        dto.getMajor(),
                        dto.getLab(),
                        dto.getPhone(),
                        dto.getEmail(),
                        dto.getDeptName(),
                        dto.getCollegeName()
                )).toList();
    }

    private String[] splitBySpace(String content) {
        return content.trim().split(SPACE_REGEX);
    }
}
