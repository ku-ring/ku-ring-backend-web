package com.kustacks.kuring.auth.userdetails;

import java.util.List;

public interface UserDetails {
    String getPrincipal();
    String getPassword();
    List<String> getAuthorities();
    boolean isValidCredentials(String credentials);
}
