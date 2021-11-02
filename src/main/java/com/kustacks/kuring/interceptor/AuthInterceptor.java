package com.kustacks.kuring.interceptor;

import com.kustacks.kuring.annotation.CheckSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Slf4j
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

        if(!(handler instanceof HandlerMethod)) {
            // return true이면  Controller에 있는 메서드가 아니므로, 그대로 컨트롤러로 진행
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        CheckSession checkSession = handlerMethod.getMethodAnnotation(CheckSession.class);
        if(checkSession == null) {
            return true;
        }

        HttpSession session = request.getSession(false);
        if(session != null) {
            log.info("sessionId = {}", session.getId());
            log.info("lastAccessedTime = {}", session.getLastAccessedTime());
        }

        boolean isSessionFromCookie = session != null;
        boolean isSessionRequired = checkSession.isSessionRequired();
        
        // 세션이 있어야 하는 경우 - 없으면 로그인 페이지로 이동
        if(isSessionRequired) {
            log.info("isSessionFromCookie = {}", isSessionFromCookie);
            if(isSessionFromCookie) {
                return true;
            } else {
                response.sendRedirect("/admin/login");
                return false;
            }
        }
        // 세션이 없어야 하는 경우(로그인 페이지) - 있으면 이전 페이지로 이동
        else {
            if(isSessionFromCookie) {
                response.setStatus(401);
                response.sendRedirect("/admin/dashboard");
                return false;
            } else {
                return true;
            }
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                            @Nullable ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                 @Nullable Exception ex) throws Exception {
    }
}
