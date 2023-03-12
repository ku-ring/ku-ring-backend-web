package com.kustacks.kuring.data;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Service
public class DatabaseCleanup implements InitializingBean {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private List<String> tableNames;

    @Override
    public void afterPropertiesSet() {
        tableNames = new ArrayList<>();
        extractTableNames();
    }

    /**
     * @version : 1.0.0
     * 각 테스트 수행직후 저장된 데이터를 전부 삭제하는 메서드
     * 테스트 케이스별 독립환경 유지를 위한 용도
     * @author : zbqmgldjfh(https://github.com/zbqmgldjfh)
     * @see : DatabaseCleanup#extractTableNames() 를 사용하여 truncate할 테이블 목록을 불러온다
     */
    @Transactional
    public void execute() {
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = FALSE");
        for (String tableName : tableNames) {
            jdbcTemplate.execute("TRUNCATE TABLE " + tableName);
        }
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = TRUE");
    }

    private void extractTableNames() {
        try {
            Connection connection = dataSource.getConnection();
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(connection.getCatalog(), null, null, new String[]{"TABLE"});
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                tableNames.add(tableName);
            }
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
