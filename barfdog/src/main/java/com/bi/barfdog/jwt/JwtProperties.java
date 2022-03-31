package com.bi.barfdog.jwt;

public interface JwtProperties {
    String SECRET = "barrrrfDog";
    int EXPIRATION_TIME = 1000*60*30;
    String TOKEN_PREFIX = "Bearer ";
    String HEADER_STRING = "Authorization";
}
