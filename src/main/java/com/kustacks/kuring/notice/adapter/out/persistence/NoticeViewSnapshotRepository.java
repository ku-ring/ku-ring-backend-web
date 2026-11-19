package com.kustacks.kuring.notice.adapter.out.persistence;

import com.kustacks.kuring.notice.domain.NoticeViewSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

interface NoticeViewSnapshotRepository extends JpaRepository<NoticeViewSnapshot, Long> {
}
