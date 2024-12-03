package com.kustacks.kuring.support;

import org.mockserver.client.MockServerClient;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.MediaType;

import static org.mockserver.matchers.Times.exactly;

public class MockServerSupport {
    public static HttpRequest getMockRequest(String httpMethod, String requestPath) {
        return HttpRequest.request(httpMethod)
                .withPath(requestPath);
    }

    public static HttpResponse getMockResponse(int statusCode, MediaType mediaType, String responseBody) {
        return HttpResponse.response()
                .withStatusCode(statusCode)
                .withBody(responseBody)
                .withContentType(mediaType);
    }

    public static void createNewMockServer(int port, HttpRequest httpRequest, HttpResponse httpResponse) {
        new MockServerClient("127.0.0.1", port)
                .when(httpRequest, exactly(1))
                .respond(httpResponse);
    }
}
