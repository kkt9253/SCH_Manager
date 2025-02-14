package sch_helper.sch_manager.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import sch_helper.sch_manager.auth.filter.CustomLogoutFilter;
import sch_helper.sch_manager.auth.filter.JwtExceptionFilter;
import sch_helper.sch_manager.auth.security.CustomAccessDeniedHandler;
import sch_helper.sch_manager.auth.security.CustomAuthenticationEntryPoint;
import sch_helper.sch_manager.auth.util.CookieUtil;
import sch_helper.sch_manager.auth.filter.JwtFilter;
import sch_helper.sch_manager.auth.util.JwtUtil;
import sch_helper.sch_manager.auth.filter.LoginFilter;
import sch_helper.sch_manager.auth.util.RefreshTokenHelper;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final RefreshTokenHelper refreshTokenHelper;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final ObjectMapper objectMapper;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {

        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(auth -> auth
                .disable());

        http.httpBasic(auth -> auth
                .disable());

        http.logout(auth -> auth
                .disable());

        http.formLogin(auth -> auth
                .disable());

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers( "/login", "/reissue").permitAll()
                .anyRequest().authenticated());

        http.addFilterBefore(new JwtExceptionFilter(objectMapper), LogoutFilter.class);

        http.addFilterAt(new CustomLogoutFilter(refreshTokenHelper, cookieUtil, jwtUtil, objectMapper), LogoutFilter.class);

        http.addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, cookieUtil, refreshTokenHelper, objectMapper), UsernamePasswordAuthenticationFilter.class);

        http.addFilterAfter(new JwtFilter(jwtUtil), LoginFilter.class);

        http.exceptionHandling(e -> e
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler));

        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
