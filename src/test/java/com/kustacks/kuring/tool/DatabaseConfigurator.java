package com.kustacks.kuring.tool;

import com.kustacks.kuring.admin.domain.Admin;
import com.kustacks.kuring.admin.domain.AdminRepository;
import com.kustacks.kuring.admin.domain.AdminRole;
import com.kustacks.kuring.notice.domain.*;
import com.kustacks.kuring.staff.domain.Staff;
import com.kustacks.kuring.staff.domain.StaffRepository;
import com.kustacks.kuring.user.domain.User;
import com.kustacks.kuring.user.domain.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class DatabaseConfigurator implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(DatabaseConfigurator.class);
    protected final String USER_FCM_TOKEN = "test_fcm_token";
    protected static final String ADMIN_ROOT_LOGIN_ID = "admin@email.com";
    protected static final String ADMIN_ROOT_PASSWORD = "admin_password";
    protected static final String ADMIN_CLIENT_LOGIN_ID = "client@email.com";
    protected static final String ADMIN_CLIENT_PASSWORD = "client_password";

    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;
    private final StaffRepository staffRepository;
    private final AdminRepository adminRepository;
    private final DepartmentNoticeRepository departmentNoticeRepository;
    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    private List<String> tableNames;

    public DatabaseConfigurator(NoticeRepository noticeRepository, UserRepository userRepository,
                                StaffRepository staffRepository, AdminRepository adminRepository,
                                DepartmentNoticeRepository departmentNoticeRepository,
                                DataSource dataSource, JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder) {
        this.noticeRepository = noticeRepository;
        this.userRepository = userRepository;
        this.staffRepository = staffRepository;
        this.adminRepository = adminRepository;
        this.departmentNoticeRepository = departmentNoticeRepository;
        this.dataSource = dataSource;
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void afterPropertiesSet() {
        tableNames = new ArrayList<>();
        extractTableNames();
        createFullTextIndex();
        log.info("[DatabaseConfigurator] afterPropertiesSet complete");
    }

    /**
     * @version : 1.1.0
     * 각 테스트 수행직후 저장된 데이터를 전부 삭제하는 메서드
     * 테스트 케이스별 독립환경 유지를 위한 용도
     * @author : zbqmgldjfh(https://github.com/zbqmgldjfh)
     * @see : DatabaseCleanup#extractTableNames() 를 사용하여 truncate할 테이블 목록을 불러온다
     */
    public void clear() {
        truncateAllTable();
    }

    /**
     * @version : 1.1.0
     * DB를 사용하는 테스트에서 초기화 데이터 삽입용 메서드
     * @author : zbqmgldjfh(https://github.com/zbqmgldjfh)
     */
    @Transactional
    public void loadData() {
        log.info("[DatabaseConfigurator] init start");

        initAdmin();
        initUser();
        initFeedback();
        initStaff();
        initNotice();

        log.info("[DatabaseConfigurator] init complete");
    }

    private void truncateAllTable() {
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
                this.tableNames.add(tableName);
            }
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    private void createFullTextIndex() {
        jdbcTemplate.execute("CREATE FULLTEXT INDEX idx_notice_subject ON notice (subject)");
    }

    private void initAdmin() {
        String encodePassword = passwordEncoder.encode(ADMIN_ROOT_PASSWORD);
        Admin admin = new Admin(ADMIN_ROOT_LOGIN_ID, encodePassword);
        admin.addRole(AdminRole.ROLE_ROOT);
        adminRepository.save(admin);

        encodePassword = passwordEncoder.encode(ADMIN_CLIENT_PASSWORD);
        admin = new Admin(ADMIN_CLIENT_LOGIN_ID, encodePassword);
        admin.addRole(AdminRole.ROLE_CLIENT);
        adminRepository.save(admin);
    }

    private void initUser() {
        User newUser = new User(USER_FCM_TOKEN);
        userRepository.save(newUser);
    }

    private void initFeedback() {
        User findUser = userRepository.findByToken(USER_FCM_TOKEN).get();
        findUser.addFeedback("test feedback 1");
        findUser.addFeedback("test feedback 2");
        findUser.addFeedback("test feedback 3");
        findUser.addFeedback("test feedback 4");
        findUser.addFeedback("test feedback 5");
    }

    private void initNotice() {
        List<Notice> noticeList = buildNotices(5, CategoryName.STUDENT);
        noticeRepository.saveAll(noticeList);

        List<DepartmentNotice> importantDeptNotices = buildDepartmentNotice(7, DepartmentName.COMPUTER, CategoryName.DEPARTMENT, true);
        departmentNoticeRepository.saveAll(importantDeptNotices);

        List<DepartmentNotice> normalDeptNotices = buildDepartmentNotice(5, DepartmentName.COMPUTER, CategoryName.DEPARTMENT, false);
        departmentNoticeRepository.saveAll(normalDeptNotices);
    }

    private void initStaff() {
        List<Staff> staffList = buildStaffs(5);
        staffRepository.saveAll(staffList);
    }

    private List<DepartmentNotice> buildDepartmentNotice(int cnt, DepartmentName departmentName, CategoryName categoryName, boolean important) {
        return Stream.iterate(0, i -> i + 1)
                .limit(cnt)
                .map(i -> new DepartmentNotice("article_" + i, "post_date_" + i, "update_date_" + i, "subject_" + i, categoryName, important, "https://www.example.com", departmentName))
                .collect(Collectors.toList());
    }

    private static List<Notice> buildNotices(int cnt, CategoryName categoryName) {
        return Stream.iterate(0, i -> i + 1)
                .limit(cnt)
                .map(i -> new Notice("article_" + i, "post_date_" + i, "update_date_" + i, "subject_" + i, categoryName, false, "https://www.example.com"))
                .collect(Collectors.toList());
    }

    private static List<Staff> buildStaffs(int cnt) {
        return Stream.iterate(0, i -> i + 1)
                .limit(cnt)
                .map(i -> new Staff("shine_" + i, "computer", "lab", "phone", "email@naver.com", "dept", "college"))
                .collect(Collectors.toList());
    }
}
