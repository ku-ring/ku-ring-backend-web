package com.kustacks.kuring.auth.interceptor;

import com.kustacks.kuring.auth.authentication.AuthenticationException;
import com.kustacks.kuring.auth.authentication.AuthenticationToken;
import com.kustacks.kuring.auth.context.Authentication;
import com.kustacks.kuring.auth.context.SecurityContextHolder;
import com.kustacks.kuring.auth.exception.UnauthorizedException;
import com.kustacks.kuring.auth.userdetails.UserDetails;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class AuthenticationChainingFilter implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (isAlreadyLoginUser()) {
            return true;
        }

        try {
            AuthenticationToken token = convert(request);
            UserDetails userDetails = findUserDetails(token);
            afterAuthentication(userDetails);
            return true;
        } catch (UnauthorizedException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        } catch (AuthenticationException e) {
            return true;
        }
    }

    private boolean isAlreadyLoginUser() {
        return SecurityContextHolder.getContext().getAuthentication() != null;
    }

    private void afterAuthentication(UserDetails userDetails) {
        assert userDetails != null;
        Authentication authentication = new Authentication(userDetails.getPrincipal(), userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    protected abstract AuthenticationToken convert(HttpServletRequest request);

    protected abstract UserDetails findUserDetails(AuthenticationToken token);
}
