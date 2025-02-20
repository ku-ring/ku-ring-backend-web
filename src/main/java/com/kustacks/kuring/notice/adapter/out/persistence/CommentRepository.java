package com.kustacks.kuring.notice.adapter.out.persistence;

import com.kustacks.kuring.notice.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

interface CommentRepository extends JpaRepository<Comment, Long>, CommentQueryRepository {
}
