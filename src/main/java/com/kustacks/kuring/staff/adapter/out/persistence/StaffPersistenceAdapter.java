package com.kustacks.kuring.staff.adapter.out.persistence;

import com.kustacks.kuring.common.annotation.PersistenceAdapter;
import com.kustacks.kuring.staff.application.port.out.StaffQueryPort;
import com.kustacks.kuring.staff.application.port.out.dto.StaffSearchDto;
import lombok.RequiredArgsConstructor;

import java.util.List;

@PersistenceAdapter
@RequiredArgsConstructor
public class StaffPersistenceAdapter implements StaffQueryPort {

    private final StaffRepository staffRepository;

    @Override
    public List<StaffSearchDto> findAllByKeywords(List<String> keywords) {
        return this.staffRepository.findAllByKeywords(keywords);
    }
}
