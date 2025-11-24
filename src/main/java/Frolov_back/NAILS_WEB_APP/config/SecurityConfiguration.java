package Frolov_back.NAILS_WEB_APP.config;


/*
* TODO: // В контроллерах добавляем аннотации
*@PreAuthorize("hasRole('ADMIN')") // Только для админов
*@PreAuthorize("hasRole('MASTER')") // Только для мастеров
*@PreAuthorize("hasRole('CLIENT')") // Только для клиентов
*@PreAuthorize("hasAnyRole('ADMIN', 'MASTER')") // Для админов и мастеров
* */
import Frolov_back.NAILS_WEB_APP.security.JwtAuthenticationFilter;
import Frolov_back.NAILS_WEB_APP.impl.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsServiceImpl userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults()) // использует WebConfig.corsConfigurationSource()
                .authorizeHttpRequests(auth -> auth
                        // ВСЁ, что начинается с /api/v1/auth/ — без токена
                        .requestMatchers("/api/v1/auth/").permitAll()

                        // Swagger
                        .requestMatchers("/swagger-ui/", "/v3/api-docs/", "/swagger-ui.html").permitAll()

                        // Обязательно разрешаем OPTIONS для всех путей (preflight)
                        .requestMatchers(HttpMethod.OPTIONS, "/").permitAll()

                        // Всё остальное — только с валидным JWT
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}