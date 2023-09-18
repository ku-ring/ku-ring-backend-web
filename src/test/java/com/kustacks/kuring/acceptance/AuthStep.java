package com.kustacks.kuring.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public class AuthStep {

    public static String 로그인_되어_있음(String loginId, String password) {
        ExtractableResponse<Response> response = 로그인_요청(loginId, password);
        return response.jsonPath().getString("accessToken");
    }

    public static ExtractableResponse<Response> 로그인_요청(String loginId, String password) {
        Map<String, String> params = new HashMap<>();
        params.put("loginId", loginId);
        params.put("password", password);

        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().post("/api/v2/admin/login")
                .then().log().all()
                .statusCode(HttpStatus.OK.value()).extract();
    }
}
