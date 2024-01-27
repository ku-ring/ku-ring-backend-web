package com.kustacks.kuring.notice.adapter.out.persistence;

import com.kustacks.kuring.notice.domain.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

interface NoticeRepository extends JpaRepository<Notice, Long>, NoticeQueryRepository {
}
