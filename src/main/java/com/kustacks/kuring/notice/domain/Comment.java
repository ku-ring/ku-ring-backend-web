package com.kustacks.kuring.notice.domain;

import com.kustacks.kuring.common.domain.BaseTimeEntity;
import com.kustacks.kuring.common.domain.Content;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseTimeEntity {

    @Id
    @Getter(AccessLevel.PRIVATE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "notice_id", nullable = false)
    private Long noticeId;

    @Embedded
    private Content content;

    public Comment(Long userId, Long noticeId, String content) {
        this.userId = userId;
        this.noticeId = noticeId;
        this.content = new Content(content);
    }

    public String getContent() {
        return content.getValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return Objects.equals(getId(), comment.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
