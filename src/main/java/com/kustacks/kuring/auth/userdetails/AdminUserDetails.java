package com.kustacks.kuring.auth.userdetails;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AdminUserDetails implements UserDetails {

    private String loginId;
    private String password;
    private List<String> authorities;

    public static AdminUserDetails of(String loginId, List<String> authorities) {
        return new AdminUserDetails(loginId, null, authorities);
    }

    @Override
    public String getPrincipal() {
        return loginId;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public List<String> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isValidCredentials(String credentials) {
        return this.password.equals(credentials);
    }
}
