package com.app.Diary.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Permite CORS en todas las rutas (ej: /api/diary, /api/users, etc.)
                // --- NOTA DE SEGURIDAD IMPORTANTE ---
                // Usar ".allowedOrigins("*")" es una configuración permisiva, ideal para el desarrollo,
                // ya que permite que tu API sea llamada desde cualquier origen (incluyendo tu app en Expo Go).
                //
                // Para una aplicación en PRODUCCIÓN en la Play Store, esto es funcional, pero la práctica
                // más segura es restringir los orígenes. Sin embargo, como una app móvil no tiene un "origen"
                // web tradicional, esta configuración es comúnmente aceptada. Si en el futuro creas una
                // versión web, deberías reemplazar "*" por la URL específica de tu web, por ejemplo:
                // .allowedOrigins("https://mi-diario-app.com")
                .allowedOrigins("*") // Permite peticiones de cualquier origen
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Permite los métodos HTTP comunes
                .allowedHeaders("*"); // Permite todas las cabeceras
    }
}
