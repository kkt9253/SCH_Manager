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
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
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

import java.util.Collections;
import java.util.List;

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
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable);

        http
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers(HttpMethod.POST, "/api/admin/week-meal-plans/HYANGSEOL1/**").hasAnyAuthority(Role.Master.name(), Role.Admin1.name(), Role.Admin2.name())
                                .requestMatchers(HttpMethod.POST, "/api/admin/week-meal-plans/FACULTY/**").hasAnyAuthority(Role.Master.name(), Role.Admin1.name(), Role.Admin3.name())
                                .requestMatchers(HttpMethod.POST, "/api/admin/meal-plans/HYANGSEOL1/**").hasAnyAuthority(Role.Master.name(), Role.Admin1.name(), Role.Admin2.name())
                                .requestMatchers(HttpMethod.POST, "/api/admin/meal-plans/FACULTY/**").hasAnyAuthority(Role.Master.name(), Role.Admin1.name(), Role.Admin3.name())
                                .requestMatchers(HttpMethod.POST, "/api/admin/early-close/HYANGSEOL1/**").hasAnyAuthority(Role.Master.name(), Role.Admin1.name(), Role.Admin2.name())
                                .requestMatchers(HttpMethod.POST, "/api/admin/early-close/FACULTY/**").hasAnyAuthority(Role.Master.name(), Role.Admin1.name(), Role.Admin3.name())
                                .requestMatchers(HttpMethod.POST, "/api/admin/total-operating-time/HYANGSEOL1/**").hasAnyAuthority(Role.Master.name(), Role.Admin1.name(), Role.Admin2.name())
                                .requestMatchers(HttpMethod.POST, "/api/admin/total-operating-time/FACULTY/**").hasAnyAuthority(Role.Master.name(), Role.Admin1.name(), Role.Admin3.name())
                                .requestMatchers(HttpMethod.GET, "/api/admin/**").hasAnyAuthority(Role.Master.name(), Role.Admin1.name(), Role.Admin2.name(), Role.Admin3.name())
                                .requestMatchers(HttpMethod.POST, "/login", "/reissue").permitAll()
                                .requestMatchers("/api/user/**").permitAll()
                                .requestMatchers("/api/master/**").hasAnyAuthority(Role.Master.name())
                                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                                .anyRequest().authenticated()
                );

        http
                .addFilterBefore(new JwtExceptionFilter(objectMapper), LogoutFilter.class)
                .addFilterAfter(new JwtFilter(jwtUtil), LoginFilter.class)
                .addFilterAt(new CustomLogoutFilter(refreshTokenHelper, cookieUtil, jwtUtil, objectMapper), LogoutFilter.class)
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, cookieUtil, refreshTokenHelper, objectMapper), UsernamePasswordAuthenticationFilter.class);

        http.exceptionHandling(e -> e
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler));

        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
