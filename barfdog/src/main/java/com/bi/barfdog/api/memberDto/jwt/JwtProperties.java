package com.bi.barfdog.api.memberDto.jwt;

public interface JwtProperties {
    String SECRET = "barrrrfDog";
    int EXPIRATION_TIME = 1000*60*60*2;
    String TOKEN_PREFIX = "Bearer ";
    String HEADER_STRING = "Authorization";
}
