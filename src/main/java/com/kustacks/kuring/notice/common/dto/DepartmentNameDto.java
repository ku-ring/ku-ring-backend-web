package com.kustacks.kuring.notice.common.dto;

import com.kustacks.kuring.worker.DepartmentName;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DepartmentNameDto {

    private String name;
    private String hostPrefix;
    private String korName;

    private DepartmentNameDto(String name, String hostPrefix, String korName) {
        this.name = name;
        this.hostPrefix = hostPrefix;
        this.korName = korName;
    }

    public static DepartmentNameDto from(DepartmentName name) {
        return new DepartmentNameDto(name.getName(), name.getHostPrefix(), name.getKorName());
    }
}
