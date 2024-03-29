package by.bsuir.restkeeper.web.config;


import by.bsuir.restkeeper.web.security.filter.JwtAuthenticationFilter;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class WebConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    /**
     * Set up filter.
     *
     * @param http HttpSecurity
     * @return SecurityFilterChain
     * @throws Exception Exception
     */
    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http)
            throws Exception {
        http.cors()
                .and()
                .csrf().disable()
                .httpBasic().disable()
                .sessionManagement().sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS
                )
                .and()
                .authorizeHttpRequests()
                .requestMatchers("/restkeeper/v1/statistics/download")
                .permitAll()
                .requestMatchers(
                        "/restkeeper/v1/auth/users/**",
                        "/restkeeper/v1/users/**",
                        "/restkeeper/v1/auth/addresses/**",
                        "/restkeeper/v1/dishes/**",
                        "/restkeeper/v1/auth/orders/**",
                        "/restkeeper/v1/statistics/**")
                .authenticated()
                .anyRequest().permitAll()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(
                        (request, response, authException) -> {
                            response.setStatus(
                                    HttpStatus.UNAUTHORIZED.value()
                            );
                            response.getWriter().write("Bad credentials");
                        }
                )
                .accessDeniedHandler((request, response, exception) -> {
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.getWriter().write("Access is denied");
                })
                .and()
                .authenticationProvider(this.authenticationProvider)
                .addFilterBefore(
                        this.jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class
                );
        return http.build();
    }

    /**
     * Set up Cors filter.
     *
     * @return CorsConfigurationSource
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration =
                new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setAllowedOrigins(
                List.of("http://localhost:4200", "/**")
        );
        corsConfiguration.setAllowedMethods(
                List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")
        );
        corsConfiguration.setAllowedHeaders(
                List.of("Authorization", "Cache-Control", "Content-Type")
        );
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

}
