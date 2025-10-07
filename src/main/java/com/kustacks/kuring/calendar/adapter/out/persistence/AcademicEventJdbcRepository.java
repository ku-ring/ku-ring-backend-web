package com.kustacks.kuring.calendar.adapter.out.persistence;

import com.kustacks.kuring.calendar.domain.AcademicEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class AcademicEventJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void saveAll(List<AcademicEvent> events) {
        if (events.isEmpty()) {
            return;
        }

        jdbcTemplate.batchUpdate("" +
                        "INSERT INTO academic_event (event_uid, summary, description, category, transparent, sequence, notify_enabled, start_time, end_time) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                new BatchPreparedStatementSetter() {

                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        AcademicEvent academicEvent = events.get(i);
                        ps.setString(1, academicEvent.getEventUid());
                        ps.setString(2, academicEvent.getSummary());
                        ps.setString(3, academicEvent.getDescription());
                        ps.setString(4, academicEvent.getCategory().name());
                        ps.setString(5, academicEvent.getTransparent().toString());
                        ps.setInt(6, academicEvent.getSequence());
                        ps.setBoolean(7, academicEvent.getNotifyEnabled());
                        ps.setTimestamp(8, Timestamp.valueOf(academicEvent.getStartTime()));
                        ps.setTimestamp(9, Timestamp.valueOf(academicEvent.getEndTime()));
                    }

                    @Override
                    public int getBatchSize() {
                        return events.size();
                    }
                }
        );
    }

    @Transactional
    public void updateAll(List<AcademicEvent> events) {
        if (events.isEmpty()) {
            return;
        }

        jdbcTemplate.batchUpdate(
                "UPDATE academic_event " +
                        "SET summary = ?, description = ?, category = ?, transparent = ?, sequence = ?, notify_enabled = ?, start_time = ?, end_time = ? " +
                        "WHERE id = ?",
                new BatchPreparedStatementSetter() {

                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        AcademicEvent academicEvent = events.get(i);
                        ps.setString(1, academicEvent.getSummary());
                        ps.setString(2, academicEvent.getDescription());
                        ps.setString(3, academicEvent.getCategory().name());
                        ps.setString(4, academicEvent.getTransparent().toString());
                        ps.setInt(5, academicEvent.getSequence());
                        ps.setBoolean(6, academicEvent.getNotifyEnabled());
                        ps.setTimestamp(7, Timestamp.valueOf(academicEvent.getStartTime()));
                        ps.setTimestamp(8, Timestamp.valueOf(academicEvent.getEndTime()));
                        ps.setLong(9, academicEvent.getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return events.size();
                    }
                }
        );
    }
}