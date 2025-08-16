package com.app.Diary.service; // Asegúrate de que el paquete sea correcto

import com.app.Diary.model.User;
import com.app.Diary.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Este método es llamado por Spring Security para cargar un usuario por su email.
     * @param email El email del usuario que intenta autenticarse.
     * @return Un objeto UserDetails que Spring Security usará para la autenticación.
     * @throws UsernameNotFoundException si el usuario no se encuentra en la base de datos.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Buscamos al usuario en nuestra base de datos por su email.
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("No se encontró un usuario con el email: " + email)
                );

        // Creamos un objeto UserDetails de Spring Security.
        // Por ahora, no estamos manejando roles o permisos complejos, así que la lista de "authorities" está vacía.
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                new ArrayList<>() // Lista de roles/permisos (authorities)
        );
    }
}
