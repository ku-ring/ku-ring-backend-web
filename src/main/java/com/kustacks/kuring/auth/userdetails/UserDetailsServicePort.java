package com.kustacks.kuring.auth.userdetails;

@FunctionalInterface
public interface UserDetailsServicePort {
    UserDetails loadUserByUsername(String email);
}
