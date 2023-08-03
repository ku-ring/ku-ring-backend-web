package com.kustacks.kuring.admin.presentation;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class AdminExceptionHandlerV1 implements ErrorController {

    @GetMapping("/error")
    public String errorHandler(HttpServletRequest req) {
        // Get status code to determine which view should be returned
        Integer statusCode = (Integer) req.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        // In this case, status code will be shown in a view

        if(statusCode == null || statusCode >= 500) {
            return "thymeleaf/500";
        } else if(statusCode == 404) {
            return "thymeleaf/404";
        } else if (statusCode == 401) {
            return "thymeleaf/401";
        } else {
            return "thymeleaf/500";
        }
    }

    public String getErrorPath() {
        return "/error";
    }
}
