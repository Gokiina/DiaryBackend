package com.app.Diary.security; // Asegúrate de que el paquete sea el correcto

import com.app.Diary.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // Inyección de dependencias a través del constructor
    public SecurityConfig(CustomUserDetailsService customUserDetailsService, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * Define un Bean para el codificador de contraseñas.
     * Se usará para cifrar las contraseñas al registrarse y para verificarlas durante el login.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Define el AuthenticationManager como un Bean para que Spring pueda inyectarlo
     * en tu AuthController y procesar las peticiones de login.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Configura la cadena de filtros de seguridad. Aquí es donde se definen las reglas
     * de acceso a los diferentes endpoints de la API.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Deshabilitamos CSRF porque usamos JWT (stateless)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // No creamos sesiones en el servidor
                .authorizeHttpRequests(auth -> auth
                        // LA REGLA CLAVE: Permite el acceso sin autenticación a todos los endpoints bajo /api/auth/
                        .requestMatchers("/api/auth/**").permitAll()
                        // Para cualquier otra petición, se requiere que el usuario esté autenticado.
                        .anyRequest().authenticated()
                )
                // Añadimos nuestro filtro personalizado (JwtAuthenticationFilter) antes del filtro estándar de Spring.
                // Este filtro se encargará de validar el token en cada petición protegida.
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
