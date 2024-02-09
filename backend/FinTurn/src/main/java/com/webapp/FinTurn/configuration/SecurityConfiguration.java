package com.webapp.FinTurn.configuration;

import com.webapp.FinTurn.constant.SecurityConstant;
import com.webapp.FinTurn.filter.AccessDeniedHandler;
import com.webapp.FinTurn.filter.AuthenticationEntryPoint;
import com.webapp.FinTurn.filter.AuthorizationFilter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@AllArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity(proxyTargetClass = true)
@Configuration
public class SecurityConfiguration {
    private AuthorizationFilter authorizationFilter;
    private AccessDeniedHandler accessDeniedHandler;
    private AuthenticationEntryPoint authenticationEntryPoint;
    private UserDetailsService userDetailsService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Bean
    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(
                        httpSecuritySessionManagementConfigurer ->
                                httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        auth -> {
                            auth.requestMatchers(SecurityConstant.PUBLIC_URLS)
                                    .permitAll();
                        }
                )
                .exceptionHandling(
                        exceptionHandlingConfigurer ->
                                exceptionHandlingConfigurer.accessDeniedHandler(
                                        accessDeniedHandler))
                .httpBasic(
                        httpBasicConfigurer ->
                                httpBasicConfigurer.authenticationEntryPoint(
                                        authenticationEntryPoint))
                .addFilterBefore(
                        authorizationFilter, UsernamePasswordAuthenticationFilter.class)
        ;
        return http.build();
    }

    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }
}
