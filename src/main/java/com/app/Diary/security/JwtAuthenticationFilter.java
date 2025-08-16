package com.app.Diary.security; // Asegúrate de que el paquete sea correcto

import com.app.Diary.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // 1. Extraer el token de la cabecera de la petición.
            String jwt = getJwtFromRequest(request);

            // 2. Validar el token.
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                // 3. Obtener el email del usuario desde el token.
                String userEmail = tokenProvider.getEmailFromJWT(jwt);

                // 4. Cargar los detalles del usuario desde la base de datos.
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(userEmail);

                // 5. Crear un objeto de autenticación.
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 6. Establecer la autenticación en el contexto de seguridad de Spring.
                // A partir de este punto, Spring Security sabe que el usuario está autenticado.
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            logger.error("No se pudo establecer la autenticación del usuario en el contexto de seguridad", ex);
        }

        // Continuar con la cadena de filtros.
        filterChain.doFilter(request, response);
    }

    // Método auxiliar para extraer el token de la cabecera "Authorization".
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
