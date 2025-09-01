package com.kustacks.kuring.support;


import com.kustacks.kuring.common.utils.validator.BadWordValidator;
import com.kustacks.kuring.message.application.service.FirebaseSubscribeService;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.testcontainers.chromadb.ChromaDBContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTestSupport {

    public static final String ADMIN_LOGIN_ID = "admin@email.com";
    public static final String ADMIN_PASSWORD = "admin_password";
    public static final String USER_FCM_TOKEN = "test_fcm_token";
    public static final String ADMIN_CLIENT_LOGIN_ID = "client@email.com";
    public static final String ADMIN_CLIENT_PASSWORD = "client_password";
    public static final String USER_EMAIL = "client@konkuk.ac.kr";
    public static final String USER_PASSWORD = "client123!";

    protected static final ChromaDBContainer chroma;

    static {
        chroma = new ChromaDBContainer(DockerImageName.parse("chromadb/chroma:1.0.8.dev12"))
                .waitingFor(Wait.forHttp("/api/v2/heartbeat"))
                .withExposedPorts(8000);
        chroma.start();
    }

    @MockBean
    protected FirebaseSubscribeService firebaseSubscribeService;

    @LocalServerPort
    protected int port;

    @Autowired
    private DatabaseConfigurator databaseConfigurator;

    @Autowired
    private BadWordValidator badWordValidator;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        databaseConfigurator.clear();
        databaseConfigurator.loadData();
        badWordValidator.wordsInit();
    }
}
