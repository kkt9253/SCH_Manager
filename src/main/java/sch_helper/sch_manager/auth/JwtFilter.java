package sch_helper.sch_manager.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import sch_helper.sch_manager.auth.util.JwtUtil;
import sch_helper.sch_manager.domain.Role;
import sch_helper.sch_manager.domain.entity.User;

import java.io.IOException;
import java.io.PrintWriter;

@Component
@AllArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = token.split(" ")[1];

        if (jwtUtil.isExpired(accessToken)) {

            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "access token expired");
            return;
        }

        if (!("access").equals(jwtUtil.getCategory(accessToken))) {

            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "invalid access token");
            return;
        }

        String username = jwtUtil.getUserName(accessToken);
        String role = jwtUtil.getRole(accessToken);

        User user = new User();
        user.setUsername(username);
        user.setPassword("tempPassword");
        user.setRole(Role.valueOf(role));

        CustomUserDetails userDetails = new CustomUserDetails(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String msg) throws IOException {

        PrintWriter writer = response.getWriter();
        writer.print(msg);
        response.setStatus(status);
    }
}
