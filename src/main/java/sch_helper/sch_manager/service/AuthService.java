package sch_helper.sch_manager.service;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import sch_helper.sch_manager.auth.util.CookieUtil;
import sch_helper.sch_manager.auth.util.JwtUtil;
import sch_helper.sch_manager.auth.util.RefreshTokenHelper;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final RefreshTokenHelper refreshTokenHelper;

    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = cookieUtil.getCookieValue(request, "refresh");

        try {
            refreshTokenHelper.validateRefreshToken(refreshToken);
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body(e.getMessage());
        }

        if (!refreshTokenHelper.isExistRefreshToken(refreshToken)) {

            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body("Invalid refresh token");
        }

        String username = jwtUtil.getUserName(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        String newAccessToken = jwtUtil.createJwt("access", username, role, 10 * 60L);
        String newRefreshToken = jwtUtil.createJwt("refresh", username, role, 7 * 24 * 60 * 60L);

        refreshTokenHelper.saveRefreshToken(newRefreshToken);

        response.setHeader("Authorization", "Bearer " + newAccessToken);
        response.addCookie(cookieUtil.createCookie("refresh", newRefreshToken, 7 * 24 * 60 * 60));

        return ResponseEntity.ok("refresh success");
    }
}
