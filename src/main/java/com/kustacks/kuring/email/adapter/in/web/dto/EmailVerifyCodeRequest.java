package com.kustacks.kuring.email.adapter.in.web.dto;

public record EmailVerifyCodeRequest(
        String email,
        String code
){
}
