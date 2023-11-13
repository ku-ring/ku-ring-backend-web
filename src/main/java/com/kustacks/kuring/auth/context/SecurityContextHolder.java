package com.kustacks.kuring.auth.context;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class SecurityContextHolder {
    public static final String SPRING_SECURITY_CONTEXT_KEY = "SECURITY_CONTEXT";
    private static final ThreadLocal<SecurityContext> contextHolder;

    static {
        contextHolder = new ThreadLocal<>();
    }

    public static void clearContext() {
        contextHolder.remove();
    }

    public static SecurityContext getContext() {
        SecurityContext ctx = contextHolder.get();

        if (ctx == null) {
            ctx = createEmptyContext();
            contextHolder.set(ctx);
        }

        return ctx;
    }

    public static void setContext(SecurityContext context) {
        if (context != null) {
            contextHolder.set(context);
        }
    }

    public static SecurityContext createEmptyContext() {
        return new SecurityContext();
    }
}
