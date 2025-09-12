package com.kustacks.kuring.support;

import com.kustacks.kuring.admin.adapter.out.persistence.AdminRepository;
import com.kustacks.kuring.admin.domain.Admin;
import com.kustacks.kuring.admin.domain.AdminRole;
import com.kustacks.kuring.calendar.adapter.out.persistence.AcademicEventRepository;
import com.kustacks.kuring.calendar.domain.AcademicEvent;
import com.kustacks.kuring.calendar.domain.Transparent;
import com.kustacks.kuring.email.adapter.out.persistence.VerificationCodeRepository;
import com.kustacks.kuring.email.domain.VerificationCode;
import com.kustacks.kuring.notice.adapter.out.persistence.BadWordRepository;
import com.kustacks.kuring.notice.adapter.out.persistence.NoticePersistenceAdapter;
import com.kustacks.kuring.notice.adapter.out.persistence.WhitelistWordRepository;
import com.kustacks.kuring.notice.domain.BadWord;
import com.kustacks.kuring.notice.domain.BadWordCategory;
import com.kustacks.kuring.notice.domain.CategoryName;
import com.kustacks.kuring.notice.domain.DepartmentName;
import com.kustacks.kuring.notice.domain.DepartmentNotice;
import com.kustacks.kuring.notice.domain.Notice;
import com.kustacks.kuring.notice.domain.WhiteWord;
import com.kustacks.kuring.staff.adapter.out.persistence.StaffRepository;
import com.kustacks.kuring.staff.domain.Staff;
import com.kustacks.kuring.user.adapter.out.persistence.UserPersistenceAdapter;
import com.kustacks.kuring.user.domain.RootUser;
import com.kustacks.kuring.user.domain.User;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Component
public class DatabaseConfigurator implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(DatabaseConfigurator.class);
    protected final String USER_FCM_TOKEN = "test_fcm_token";
    protected static final String ADMIN_ROOT_LOGIN_ID = "admin@email.com";
    protected static final String ADMIN_ROOT_PASSWORD = "admin_password";
    protected static final String ADMIN_CLIENT_LOGIN_ID = "client@email.com";
    protected static final String ADMIN_CLIENT_PASSWORD = "client_password";
    protected static final String ROOT_USER_EMAIL = "client@konkuk.ac.kr";
    protected static final String ROOT_USER_PASSWORD = "client123!";
    protected static final String ROOT_USER_NICKNAME = "쿠링이000001";

    private final NoticePersistenceAdapter noticePersistenceAdapter;
    private final UserPersistenceAdapter userPersistenceAdapter;
    private final StaffRepository staffRepository;
    private final AdminRepository adminRepository;
    private final VerificationCodeRepository verificationCodeRepository;
    private final AcademicEventRepository academicEventRepository;
    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;
    private final BadWordRepository badWordRepository;
    private final WhitelistWordRepository whitelistWordRepository;

    private List<String> tableNames;

    public DatabaseConfigurator(NoticePersistenceAdapter noticePersistenceAdapter, UserPersistenceAdapter userPersistenceAdapter,
                                StaffRepository staffRepository, AdminRepository adminRepository, VerificationCodeRepository verificationCodeRepository,
                                AcademicEventRepository academicEventRepository, DataSource dataSource, JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder,
                                BadWordRepository badWordRepository, WhitelistWordRepository whitelistWordRepository) {
        this.noticePersistenceAdapter = noticePersistenceAdapter;
        this.userPersistenceAdapter = userPersistenceAdapter;
        this.staffRepository = staffRepository;
        this.adminRepository = adminRepository;
        this.verificationCodeRepository = verificationCodeRepository;
        this.academicEventRepository = academicEventRepository;
        this.dataSource = dataSource;
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
        this.badWordRepository = badWordRepository;
        this.whitelistWordRepository = whitelistWordRepository;
    }

    @Override
    public void afterPropertiesSet() {
        tableNames = new ArrayList<>();
        extractTableNames();
        createFullTextIndex();
        setCharsetAllTable();
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
        initRootUser();
        initUserCategory();
        initFeedback();
        initStaff();
        initNotice();
        initAcademicEvents();
        initVerificationCode();
        initBadWords();
        initWhitelistWords();

        log.info("[DatabaseConfigurator] init complete");
    }

    private void setCharsetAllTable() {
        for (String tableName : tableNames) {
            jdbcTemplate.execute("ALTER TABLE " + tableName + " CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
        }
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
        userPersistenceAdapter.save(newUser);
    }

    private void initRootUser() {
        String encodedPassword = passwordEncoder.encode(ROOT_USER_PASSWORD);
        RootUser rootUser = new RootUser(ROOT_USER_EMAIL, encodedPassword, ROOT_USER_NICKNAME);
        userPersistenceAdapter.saveRootUser(rootUser);
    }

    private void initUserCategory() {
        User findUser = userPersistenceAdapter.findByToken(USER_FCM_TOKEN).get();
        findUser.subscribeCategory(CategoryName.STUDENT);
        findUser.subscribeCategory(CategoryName.BACHELOR);
    }

    private void initFeedback() {
        User findUser = userPersistenceAdapter.findByToken(USER_FCM_TOKEN).get();
        findUser.addFeedback("test feedback 1");
        findUser.addFeedback("test feedback 2");
        findUser.addFeedback("test feedback 3");
        findUser.addFeedback("test feedback 4");
        findUser.addFeedback("test feedback 5");
    }

    private void initNotice() {
        // 카테고리 공지
        List<Notice> noticeList = buildNotices(5, CategoryName.STUDENT);
        noticePersistenceAdapter.saveAllCategoryNotices(noticeList);

        // 대학원 중요 공지
        List<DepartmentNotice> importantGradDeptNotices =
                buildImportantDepartmentNotice(7, DepartmentName.COMPUTER, CategoryName.DEPARTMENT, true, true);
        noticePersistenceAdapter.saveAllDepartmentNotices(importantGradDeptNotices);

        // 학부 중요 공지
        List<DepartmentNotice> importantUnderDeptNotices =
                buildImportantDepartmentNotice(7, DepartmentName.COMPUTER, CategoryName.DEPARTMENT, true, false);
        noticePersistenceAdapter.saveAllDepartmentNotices(importantUnderDeptNotices);

        // 대학원 일반 공지
        List<DepartmentNotice> normalGradDeptNotices =
                buildNormalDepartmentNotice(5, DepartmentName.COMPUTER, CategoryName.DEPARTMENT, false, true);
        noticePersistenceAdapter.saveAllDepartmentNotices(normalGradDeptNotices);

        // 학부 일반 공지
        List<DepartmentNotice> normalUnderDeptNotices =
                buildNormalDepartmentNotice(5, DepartmentName.COMPUTER, CategoryName.DEPARTMENT, false, false);
        noticePersistenceAdapter.saveAllDepartmentNotices(normalUnderDeptNotices);
    }

    private void initStaff() {
        List<Staff> staffList = buildStaffs(5);
        staffRepository.saveAll(staffList);
    }

    private void initVerificationCode() {
        VerificationCode verificationCode = new VerificationCode(ROOT_USER_EMAIL, "123456");
        verificationCodeRepository.save(verificationCode);
    }

    private void initBadWords() {
        BadWord badWord1 = new BadWord("병신", BadWordCategory.PROFANITY, "욕설", true);
        BadWord badWord2 = new BadWord("ㅂㅅ", BadWordCategory.PROFANITY, "욕설", true);
        BadWord badWord3 = new BadWord("시발", BadWordCategory.PROFANITY, "욕설", true);

        badWordRepository.saveAll(List.of(badWord1, badWord2, badWord3));
    }

    private void initWhitelistWords() {
        WhiteWord whiteWord1 = new WhiteWord("시발점", "욕설", true);
        WhiteWord whiteWord2 = new WhiteWord("시발자동차", "욕설", true);

        whitelistWordRepository.saveAll(List.of(whiteWord1, whiteWord2));
    }


    private List<DepartmentNotice> buildImportantDepartmentNotice(int cnt, DepartmentName departmentName, CategoryName categoryName, boolean important, boolean graduated) {
        return Stream.iterate(0, i -> i + 1)
                .limit(cnt)
                .map(i -> new DepartmentNotice((graduated ? "grad_depart_import_article_" : "depart_import_article_") + i, "2023-04-03 00:00:1" + i, "2023-04-03 00:00:1" + i, "subject_" + i, categoryName, important, "https://www.example.com", departmentName, graduated))
                .toList();
    }

    private List<DepartmentNotice> buildNormalDepartmentNotice(int cnt, DepartmentName departmentName, CategoryName categoryName, boolean important, boolean graduated) {
        return Stream.iterate(0, i -> i + 1)
                .limit(cnt)
                .map(i -> new DepartmentNotice((graduated ? "grad_depart_normal_article_" : "depart_normal_article_") + i, "2023-04-03 00:00:1" + i, "2023-04-03 00:00:1" + i, "subject_" + i, categoryName, important, "https://www.example.com", departmentName, graduated))
                .toList();
    }

    private static List<Notice> buildNotices(int cnt, CategoryName categoryName) {
        return Stream.iterate(0, i -> i + 1)
                .limit(cnt)
                .map(i -> new Notice("article_" + i, "2023-04-03 00:00:1" + i, "2023-04-03 00:00:1" + i, "subject_" + i, categoryName, false, "https://www.example.com"))
                .toList();
    }

    private static List<Staff> buildStaffs(int cnt) {
        return Stream.iterate(0, i -> i + 1)
                .limit(cnt)
                .map(i -> Staff.builder()
                        .name("shine_" + i)
                        .major("computer")
                        .lab("lab")
                        .phone("02-123-4568")
                        .email("email@naver.com")
                        .dept("dept")
                        .college("이과대학")
                        .build()
                ).toList();
    }

    private void initAcademicEvents() {
        List<AcademicEvent> academicEvents = buildAcademicEvents();
        academicEventRepository.saveAll(academicEvents);
    }

    private List<AcademicEvent> buildAcademicEvents() {
        String[][] eventData = {
                {"2월 말 행사", "2월 28일 종료되는 행사", "ACADEMIC", "TRANSPARENT", "2025-02-28T10:00", "2025-02-28T11:00"},
                {"3월 1일 개강", "3월 1일 정확히 시작하는 개강", "ACADEMIC", "TRANSPARENT", "2025-03-01T09:00", "2025-03-01T10:00"},
                {"3월 15일 중간 행사", "3월 중순 행사", "EVENT", "OPAQUE", "2025-03-15T14:00", "2025-03-15T15:00"},
                {"3월 31일 마감", "3월 마지막 날 정확히 끝나는 행사", "DEADLINE", "OPAQUE", "2025-03-31T23:00", "2025-03-31T23:59"},
                {"4월 1일 마지막 행사", "4월 1일 마지막", "ACADEMIC", "TRANSPARENT", "2025-04-01T00:00", "2025-04-01T01:00"}
        };

        return Stream.iterate(0, i -> i + 1)
                .limit(eventData.length)
                .map(i -> {
                    String[] data = eventData[i];
                    return AcademicEvent.builder()
                            .eventUid(UUID.randomUUID().toString())
                            .summary(data[0])
                            .description(data[1])
                            .category(data[2])
                            .transparent(Transparent.valueOf(data[3]))
                            .sequence(1)
                            .notifyEnabled(true)
                            .startTime(LocalDateTime.parse(data[4]))
                            .endTime(LocalDateTime.parse(data[5]))
                            .build();
                })
                .toList();
    }
}
