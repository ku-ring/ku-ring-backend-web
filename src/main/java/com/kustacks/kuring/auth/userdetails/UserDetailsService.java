package com.kustacks.kuring.auth.userdetails;

@FunctionalInterface
public interface UserDetailsService {
    UserDetails loadUserByUsername(String email);
}
