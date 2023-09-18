package com.kustacks.kuring.auth.authentication;

public enum AuthorizationType {
    BASIC, BEARER;

    public String toLowerCase() {
        return this.name().toLowerCase();
    }
}
