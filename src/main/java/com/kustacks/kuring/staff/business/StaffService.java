package com.kustacks.kuring.staff.business;

import com.kustacks.kuring.staff.common.dto.StaffSearchDto;
import com.kustacks.kuring.staff.domain.Staff;
import com.kustacks.kuring.staff.domain.StaffRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Service
public class StaffService {

    private static final String SPACE_REGEX = "[\\s+]";

    private final StaffRepository staffRepository;

    public StaffService(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }


    public List<Staff> handleSearchRequest(String keywords) {

        keywords = keywords.trim();
        String[] splitedKeywords = keywords.split("[\\s+]");

        return getStaffsByNameOrDeptOrCollege(splitedKeywords);
    }

    public List<StaffSearchDto> findAllStaffByContent(String content) {
        List<String> splitedKeywords = Arrays.asList(splitBySpace(content));

        return staffRepository.findAllByKeywords(splitedKeywords);
    }

    private List<Staff> getStaffsByNameOrDeptOrCollege(String[] keywords) {

        List<Staff> staffs = staffRepository.findByNameContainingOrDeptContainingOrCollegeContaining(keywords[0], keywords[0], keywords[0]);
        Iterator<Staff> iterator = staffs.iterator();

        for(int i=1; i<keywords.length; ++i) {
            while(iterator.hasNext()) {
                Staff staff = iterator.next();
                String curKeyword = keywords[i];

                if(staff.getName().contains(curKeyword) || staff.getDept().contains(curKeyword) || staff.getCollege().contains(curKeyword)) {

                } else {
                    iterator.remove();
                }
            }
        }

        return staffs;
    }

    private static String[] splitBySpace(String content) {
        return content.trim().split(SPACE_REGEX);
    }
}
