package com.kustacks.kuring.user.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bookmarks implements Serializable {

    private static final int MAX_BOOKMARK_SIZE = 10;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "user_bookmarks",
            joinColumns = @JoinColumn(name = "id")
    )
    @Column(name = "notice_id")
    private Set<String> bookmark = new HashSet<>();

    public void add(String noticeId) {
        if(isInvalidSize()) {
            throw new IllegalArgumentException("북마크가 저장 가능한 사이즈를 초과하였습니다.");
        }
        this.bookmark.add(noticeId);
    }

    public List<String> lookupAllId() {
        return List.copyOf(this.bookmark);
    }

    private boolean isInvalidSize() {
        return this.bookmark.size() == MAX_BOOKMARK_SIZE;
    }
}
