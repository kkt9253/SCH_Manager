package sch_helper.sch_manager.configuration;

import lombok.AllArgsConstructor;
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
import sch_helper.sch_manager.auth.CustomLogoutFilter;
import sch_helper.sch_manager.auth.util.CookieUtil;
import sch_helper.sch_manager.auth.JwtFilter;
import sch_helper.sch_manager.auth.util.JwtUtil;
import sch_helper.sch_manager.auth.LoginFilter;
import sch_helper.sch_manager.auth.util.RefreshTokenHelper;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final RefreshTokenHelper refreshTokenHelper;

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

        http.addFilterAt(new CustomLogoutFilter(refreshTokenHelper, cookieUtil), LogoutFilter.class);

        http.addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, cookieUtil, refreshTokenHelper), UsernamePasswordAuthenticationFilter.class);

        http.addFilterAfter(new JwtFilter(jwtUtil), LoginFilter.class);

        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
