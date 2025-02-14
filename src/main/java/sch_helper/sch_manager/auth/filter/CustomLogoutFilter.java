package sch_helper.sch_manager.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import sch_helper.sch_manager.auth.util.CookieUtil;
import sch_helper.sch_manager.auth.util.JwtUtil;
import sch_helper.sch_manager.auth.util.RefreshTokenHelper;
import sch_helper.sch_manager.common.response.SuccessResponse;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {

    private final RefreshTokenHelper refreshTokenHelper;
    private final CookieUtil cookieUtil;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        doFilter((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse, filterChain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        System.out.println("CustomLogoutFilter.doFilter1");
        if (!isLogoutRequest(request)) {

            filterChain.doFilter(request, response);
            return;
        }

        String refreshToken = cookieUtil.getCookieValue(request, "refresh");

        jwtUtil.validateToken(refreshToken);
        refreshTokenHelper.validateRefreshToken(refreshToken);

        refreshTokenHelper.deleteRefreshToken(refreshToken);

        Cookie cookie = cookieUtil.createCookie("refresh", null, 0);
        response.addCookie(cookie);

        String responseBody = objectMapper.writeValueAsString(SuccessResponse.of("logout success"));
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(responseBody);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    private boolean isLogoutRequest(HttpServletRequest request) {

        String requestURI = request.getRequestURI();
        String requestMethod = request.getMethod();

        return requestURI.matches("^/logout$") && requestMethod.equals("GET");
    }
}
