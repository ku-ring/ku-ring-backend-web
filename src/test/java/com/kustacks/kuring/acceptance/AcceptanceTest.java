package com.kustacks.kuring.acceptance;


import com.kustacks.kuring.tool.DatabaseConfigurator;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(locations = "classpath:test-constants.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AcceptanceTest {
    protected static final String ADMIN_LOGIN_ID = "admin@email.com";
    protected static final String ADMIN_PASSWORD = "admin_password";
    protected static final String USER_FCM_TOKEN = "test_fcm_token";
    protected static final String INVALID_USER_FCM_TOKEN = "invalid_fcm_token";
    protected static final String ADMIN_CLIENT_LOGIN_ID = "client@email.com";
    protected static final String ADMIN_CLIENT_PASSWORD = "client_password";

    @LocalServerPort
    int port;

    @Autowired
    private DatabaseConfigurator databaseConfigurator;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        databaseConfigurator.clear();
        databaseConfigurator.loadData();
    }
}
