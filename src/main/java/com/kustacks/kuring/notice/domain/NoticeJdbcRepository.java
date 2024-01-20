package com.kustacks.kuring.notice.domain;

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
public class NoticeJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void saveAllCategoryNotices(List<Notice> notices) {
        jdbcTemplate.batchUpdate("INSERT INTO notice (article_id, category_name, important, posted_dt, subject, updated_dt, url, dtype) values (?, ?, ?, ?, ?, ?, ?, 'Notice')",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        Notice notice = notices.get(i);
                        ps.setString(1, notice.getArticleId());
                        ps.setString(2, notice.getCategoryName().toUpperCase());
                        ps.setInt(3, notice.isImportant() ? 1 : 0);
                        ps.setString(4, notice.getPostedDate());
                        ps.setString(5, notice.getSubject());
                        ps.setString(6, notice.getUpdatedDate());
                        ps.setString(7, notice.getUrl());
                    }

                    @Override
                    public int getBatchSize() {
                        return notices.size();
                    }
                });
    }

    @Transactional
    public void saveAllDepartmentNotices(List<DepartmentNotice> departmentNotices) {
        jdbcTemplate.batchUpdate("INSERT INTO notice (article_id, category_name, important, posted_dt, subject, updated_dt, url, department_name, dtype) values (?, ?, ?, ?, ?, ?, ?, 'DepartmentNotice')",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        DepartmentNotice departmentNotice = departmentNotices.get(i);
                        ps.setString(1, departmentNotice.getArticleId());
                        ps.setString(2, departmentNotice.getCategoryName().toUpperCase());
                        ps.setInt(3, departmentNotice.isImportant() ? 1 : 0);
                        ps.setString(4, departmentNotice.getPostedDate());
                        ps.setString(5, departmentNotice.getSubject());
                        ps.setString(6, departmentNotice.getUpdatedDate());
                        ps.setString(7, departmentNotice.getUrl());
                        ps.setString(8, departmentNotice.getDepartmentName());
                    }

                    @Override
                    public int getBatchSize() {
                        return departmentNotices.size();
                    }
                });
    }
}
