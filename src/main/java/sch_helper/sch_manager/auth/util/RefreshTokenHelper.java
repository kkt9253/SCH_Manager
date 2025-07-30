package sch_helper.sch_manager.auth.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import sch_helper.sch_manager.common.exception.custom.JwtAuthenticationException;
import sch_helper.sch_manager.common.exception.error.ErrorCode;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RefreshTokenHelper {

    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;
    private final long REFRESH_TOKEN_TTL = 7 * 24 * 60 * 60;

    public void validateRefreshToken(String refreshToken) {

        jwtUtil.validateToken(refreshToken);
        String category = jwtUtil.getCategory(refreshToken);
        if (!"refresh".equals(category)) {
            throw new JwtAuthenticationException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        if (!isExistRefreshToken(refreshToken)) {
            throw new JwtAuthenticationException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }
    }

    public void saveRefreshToken(String refreshToken) {

        String username = jwtUtil.getUserName(refreshToken);
        String redisKey = "refresh_token:" + username;

        redisTemplate.opsForHash().put(redisKey, "refreshToken", refreshToken);
        redisTemplate.expire(redisKey, REFRESH_TOKEN_TTL, TimeUnit.SECONDS);
    }

    public boolean isExistRefreshToken(String refreshToken) {

        String username = jwtUtil.getUserName(refreshToken);
        String redisKey = "refresh_token:" + username;
        String storedToken = (String) redisTemplate.opsForHash().get(redisKey, "refreshToken");

        return storedToken != null && storedToken.equals(refreshToken);
    }

    public void deleteRefreshToken(String refreshToken) {

        String username = jwtUtil.getUserName(refreshToken);
        String redisKey = "refresh_token:" + username;

        redisTemplate.delete(redisKey);
    }
}
