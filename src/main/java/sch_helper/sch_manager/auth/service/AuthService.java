package sch_helper.sch_manager.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import sch_helper.sch_manager.auth.util.CookieUtil;
import sch_helper.sch_manager.auth.util.JwtUtil;
import sch_helper.sch_manager.auth.util.RefreshTokenHelper;
import sch_helper.sch_manager.common.response.SuccessResponse;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final RefreshTokenHelper refreshTokenHelper;

    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = cookieUtil.getCookieValue(request, "refresh");

        refreshTokenHelper.validateRefreshToken(refreshToken);

        String username = jwtUtil.getUserName(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        String newAccessToken = jwtUtil.createJwt("access", username, role, 24 * 60 * 60L); // 10 * 60L
        String newRefreshToken = jwtUtil.createJwt("refresh", username, role, 7 * 24 * 60 * 60L);

        refreshTokenHelper.saveRefreshToken(newRefreshToken);

        response.setHeader("Authorization", "Bearer " + newAccessToken);
        response.addCookie(cookieUtil.createCookie("refresh", newRefreshToken, 7 * 24 * 60 * 60));

        return ResponseEntity.ok(SuccessResponse.ok("Access token reissued"));
    }
}
