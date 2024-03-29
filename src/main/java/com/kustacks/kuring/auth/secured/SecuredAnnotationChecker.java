package com.kustacks.kuring.auth.secured;

import com.kustacks.kuring.auth.context.Authentication;
import com.kustacks.kuring.auth.context.SecurityContextHolder;
import com.kustacks.kuring.auth.exception.RoleAuthenticationException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

@Aspect
@Component
public class SecuredAnnotationChecker {

    private static final String ROLE_CLIENT = "ROLE_CLIENT";
    private static final String ROLE_ROOT = "ROLE_ROOT";

    @Before("@annotation(com.kustacks.kuring.auth.secured.Secured)")
    public void checkAuthorities(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Secured secured = method.getAnnotation(Secured.class);
        List<String> values = Arrays.stream(secured.value())
                .map(String::valueOf)
                .toList();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(isRoleRoot(authentication)) {
            authentication.getAuthorities().add(ROLE_CLIENT);
        }

        authentication.getAuthorities().stream()
                .filter(values::contains)
                .findFirst()
                .orElseThrow(() -> new RoleAuthenticationException("권한이 없습니다."));
    }

    private static boolean isRoleRoot(Authentication authentication) {
        return authentication.getAuthorities().size() == 1 && authentication.getAuthorities().contains(ROLE_ROOT);
    }
}
