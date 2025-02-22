package sch_helper.sch_manager.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
import sch_helper.sch_manager.domain.user.enums.Role;

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
                .requestMatchers( HttpMethod.POST, "/login", "/reissue").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/admin/week-meal-plans/hyangseol1").hasAnyAuthority(Role.Master.name(), Role.Admin1.name(), Role.Admin2.name())
                .requestMatchers(HttpMethod.POST, "/api/admin/week-meal-plans/faculty").hasAnyAuthority(Role.Master.name(), Role.Admin1.name(), Role.Admin3.name())
                .requestMatchers(HttpMethod.POST, "/api/admin/meal-plans/hyangseol1").hasAnyAuthority(Role.Master.name(), Role.Admin1.name(), Role.Admin2.name())
                .requestMatchers(HttpMethod.POST, "/api/admin/meal-plans/faculty").hasAnyAuthority(Role.Master.name(), Role.Admin1.name(), Role.Admin3.name())
                .requestMatchers(HttpMethod.POST, "/api/master/meal-plans/hyangseol1").hasAnyAuthority(Role.Master.name())
                .requestMatchers(HttpMethod.POST, "/api/master/meal-plans/faculty").hasAnyAuthority(Role.Master.name())
                // 추가된 GET - api 설정해야 함 (근데 설정하기 애매해서 고민 중)
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
