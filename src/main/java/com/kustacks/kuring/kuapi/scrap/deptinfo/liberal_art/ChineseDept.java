package com.kustacks.kuring.kuapi.scrap.deptinfo.liberal_art;

import org.springframework.stereotype.Component;

// TODO: 얘는 siteId가 없고, menuSeq가 아니라 menuId임..
@Component
public class ChineseDept extends LiberalArtCollege {

    public ChineseDept() {
        super("121255", "중어중문학과", "3086");
    }
}
