package com.kustacks.kuring.notice.adapter.out.persistence;

import com.kustacks.kuring.notice.domain.DepartmentName;
import com.kustacks.kuring.notice.domain.DepartmentNotice;
import com.kustacks.kuring.notice.domain.Notice;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
class NoticeJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void saveAllCategoryNotices(List<Notice> notices) {
        jdbcTemplate.batchUpdate("INSERT INTO notice (article_id, category_name, important, embedded, posted_dt, subject, updated_dt, url, graduated, dtype) values (?, ?, ?, ?, ?, ?, ?, ?, ?, 'Notice')",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        Notice notice = notices.get(i);
                        ps.setString(1, notice.getArticleId());
                        ps.setString(2, notice.getCategoryName().toUpperCase());
                        ps.setInt(3, notice.isImportant() ? 1 : 0);
                        ps.setInt(4, notice.isEmbedded() ? 1 : 0);
                        ps.setString(5, notice.getPostedDate());
                        ps.setString(6, notice.getSubject());
                        ps.setString(7, notice.getUpdatedDate());
                        ps.setString(8, notice.getUrl());
                        ps.setInt(9, 0);
                    }

                    @Override
                    public int getBatchSize() {
                        return notices.size();
                    }
                });
    }

    @Transactional
    public void saveAllDepartmentNotices(List<DepartmentNotice> departmentNotices) {
        jdbcTemplate.batchUpdate(
                "INSERT INTO notice (article_id, category_name, important, embedded, posted_dt, subject, updated_dt, url, department_name, graduated, dtype) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        DepartmentNotice departmentNotice = departmentNotices.get(i);
                        ps.setString(1, departmentNotice.getArticleId());
                        ps.setString(2, departmentNotice.getCategoryName().toUpperCase());
                        ps.setInt(3, departmentNotice.isImportant() ? 1 : 0);
                        ps.setInt(4, departmentNotice.isEmbedded() ? 1 : 0);
                        ps.setString(5, departmentNotice.getPostedDate());
                        ps.setString(6, departmentNotice.getSubject());
                        ps.setString(7, departmentNotice.getUpdatedDate());
                        ps.setString(8, departmentNotice.getUrl());
                        ps.setString(9, DepartmentName.fromName(departmentNotice.getDepartmentName()).name());
                        ps.setBoolean(10, departmentNotice.getGraduated());
                        ps.setString(11, "DepartmentNotice");
                    }

                    @Override
                    public int getBatchSize() {
                        return departmentNotices.size();
                    }
                });
    }

}
