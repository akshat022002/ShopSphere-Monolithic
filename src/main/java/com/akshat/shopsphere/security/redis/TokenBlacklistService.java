package com.akshat.shopsphere.security.redis;

public interface TokenBlacklistService {

    void BlacklistToken(String token);

    boolean isBlacklisted(String token);
}
