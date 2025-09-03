package com.app.Diary.service;

import com.app.Diary.model.User;
import com.app.Diary.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. Buscamos el usuario en nuestra base de datos por su email.
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No se encontró un usuario con el email: " + email));

        // --- ESTA ES LA CORRECCIÓN FINAL ---
        // 2. Verificamos si el usuario tiene una contraseña.
        // Los usuarios de Google no tendrán una, por lo que user.getPassword() será null.
        String password = user.getPassword();
        if (password == null) {
            // Si no hay contraseña, usamos una cadena vacía como placeholder.
            // El constructor de User de Spring Security no acepta null.
            password = "";
        }

        // 3. Creamos y devolvemos el objeto UserDetails que Spring Security necesita,
        // usando la contraseña (real o el placeholder vacío).
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                password,
                Collections.emptyList() // Asumimos que no usamos roles por ahora
        );
    }
}
