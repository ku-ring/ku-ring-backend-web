package com.kustacks.kuring.notice.domain;

import com.kustacks.kuring.common.domain.BaseTimeEntity;
import com.kustacks.kuring.common.domain.Content;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@SQLDelete(sql = "UPDATE comment SET destroyed_at = CURRENT_TIMESTAMP where id = ?")
@SQLRestriction("destroyed_at IS NULL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "parent_id", nullable = true)
    private Long parentId;

    @Column(name = "root_user_id", nullable = false)
    private Long rootUserId;

    @Column(name = "notice_id", nullable = false)
    private Long noticeId;

    @Embedded
    private Content content;

    @Column(name = "destroyed_at", nullable = true)
    private LocalDateTime destroyedAt;

    public Comment(Long rootUserId, Long noticeId, String content) {
        this.rootUserId = rootUserId;
        this.noticeId = noticeId;
        this.content = new Content(content);
    }

    public Comment(Long rootUserId, Long noticeId, Long parentId, String content) {
        this.rootUserId = rootUserId;
        this.noticeId = noticeId;
        this.parentId = parentId;
        this.content = new Content(content);
    }

    public String getContent() {
        return content.getValue();
    }

    public void editContent(String content) {
        this.content = new Content(content);
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
