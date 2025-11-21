package com.partcraft.back.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@RedisHash("refresh_tokens")
@NoArgsConstructor
@Getter
@Setter
public class RefreshToken {
    @Id
    private String token;

    private Long userId;

    private Instant expiryDate;

    @TimeToLive(unit = TimeUnit.SECONDS)
    private Long ttl;

    public boolean isExpired() {
        return expiryDate.isBefore(Instant.now());
    }

    public RefreshToken(String token, Long userId, Instant expiryDate) {
        this.token = token;
        this.userId = userId;
        this.expiryDate = expiryDate;
        this.ttl = expiryDate.getEpochSecond() - Instant.now().getEpochSecond();
    }
}
