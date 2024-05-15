package com.adagency.config;

import com.adagency.dbwork.service.CustomUserDetailsService;
import com.adagency.model.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AnonymousAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.ArrayList;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@EnableWebMvc
public class SecurityConfig {
    
    private final CustomUserDetailsService customUserDetailsService;
    
    @Autowired
    public SecurityConfig(CustomUserDetailsService customUserDetailsService){
        this.customUserDetailsService = customUserDetailsService;
    }
    
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .anonymous(anonymous -> anonymous.authenticationFilter(anonymousAuthenticationFilter()))
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/", "/register", "/login", "/logout").permitAll()
                        .requestMatchers("/mainmenu", "/profile/*", "/company").authenticated()
                        .requestMatchers("/services/**", "/users", "/company/createcompany").hasAnyRole("AGENT", "ADMIN")
                        .requestMatchers("/registerworker").hasAnyRole("ADMIN")
                        .anyRequest().authenticated())
                .formLogin((form) -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login.html")
                        .permitAll()
                        .defaultSuccessUrl("/", true)
                        .successHandler((request, response, authentication) -> {
                            SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
                            if (savedRequest != null) {
                                String targetUrl = savedRequest.getRedirectUrl();
                                if (!targetUrl.endsWith("/login") && !targetUrl.endsWith("/register") && !targetUrl.endsWith("/logout")) {
                                    response.sendRedirect(targetUrl);
                                } else {
                                    response.sendRedirect("/");
                                }
                            } else {
                                response.sendRedirect("/");
                            }
                        }))
                .logout((logout) -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"))
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .invalidSessionUrl("/login")
                        .maximumSessions(1)
                        .expiredUrl("/login"))
                .sessionManagement(management -> management.enableSessionUrlRewriting(false));
        return http.build();
    }
    
    
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailsService).passwordEncoder(bCryptPasswordEncoder());
    }
    
    @Bean
    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    
    
    @Bean
    public AnonymousAuthenticationFilter anonymousAuthenticationFilter() {
        UserDetails anonymousUser = new CustomUserDetails("anonymousUser", "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ANONYMOUS")), -1L);
        return new AnonymousAuthenticationFilter("key", anonymousUser, new ArrayList<>(anonymousUser.getAuthorities()));
    }
    
    @Bean
    public AnonymousAuthenticationProvider anonymousAuthenticationProvider() {
        return new AnonymousAuthenticationProvider("key");
    }
    
}