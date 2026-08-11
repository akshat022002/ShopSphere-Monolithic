package com.akshat.shopsphere.security.redis;

import com.akshat.shopsphere.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class TokenBlacklistServiceImpl implements TokenBlacklistService{

    private final RedisTemplate<String,Object> redisTemplate;
    private final JwtService jwtService;

    String BLACKLIST_PREFIX= "blacklist:";

    @Override
    public void BlacklistToken(String token) {



        Date expiration = jwtService.extractExpiration(token);

        long ttl = expiration.getTime()- System.currentTimeMillis();

        if(ttl>0){
            redisTemplate.opsForValue()
                    .set(BLACKLIST_PREFIX+token,
                    "BLACKLISTED",
                    Duration.ofMillis(ttl));
        }
    }

    @Override
    public boolean isBlacklisted(String token) {
        return Boolean.TRUE
                .equals(redisTemplate.hasKey(BLACKLIST_PREFIX+token));
    }
}
