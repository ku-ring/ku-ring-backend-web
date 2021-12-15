package com.kustacks.kuring.kuapi.staff.deptinfo.real_estate;

import org.springframework.stereotype.Component;

@Component
public class RealEstateDept extends RealEstateCollege {

    // 부동산학과는 교수진 정보를 렌더링하는 방법이 다름. 따라서 pfForumId 인자를 전달하지 않았다.
    public RealEstateDept() {
        super("127426", "부동산학과");
    }
}
