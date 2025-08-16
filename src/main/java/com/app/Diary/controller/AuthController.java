package com.app.Diary.controller; // Asegúrate de que el paquete sea correcto

import com.app.Diary.model.AuthProvider;
import com.app.Diary.model.User;
import com.app.Diary.payload.AuthResponse;
import com.app.Diary.payload.GoogleTokenRequest;
import com.app.Diary.payload.LoginRequest;
import com.app.Diary.payload.SignUpRequest;
import com.app.Diary.repository.UserRepository;
import com.app.Diary.security.JwtTokenProvider;

// --- NUEVOS IMPORTS PARA GOOGLE ---
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenProvider tokenProvider;

    // --- NUEVO: Inyectamos el Google Client ID desde application.properties ---
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;


    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        // ... (código existente sin cambios)
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignUpRequest signUpRequest) {
        // ... (código existente sin cambios)
    }


    // --- NUEVO ENDPOINT PARA LOGIN CON GOOGLE ---
    @PostMapping("/google")
    public ResponseEntity<?> authenticateUserWithGoogle(@RequestBody GoogleTokenRequest googleTokenRequest) {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        GoogleIdToken idToken;
        try {
            // Verificamos el token que nos llega del frontend
            idToken = verifier.verify(googleTokenRequest.getToken());
        } catch (Exception e) {
            return new ResponseEntity<>("Token de Google inválido", HttpStatus.UNAUTHORIZED);
        }

        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String name = (String) payload.get("name");

            // Comprobamos si el usuario ya existe en nuestra BD
            Optional<User> userOptional = userRepository.findByEmail(email);
            User user;

            if (userOptional.isPresent()) {
                // Si el usuario existe, lo usamos
                user = userOptional.get();
            } else {
                // Si no existe, creamos uno nuevo
                user = new User();
                user.setEmail(email);
                user.setName(name);
                user.setProvider(AuthProvider.google);
                // Para usuarios de Google, no necesitamos guardar una contraseña
                userRepository.save(user);
            }

            // Creamos una "autenticación" para el usuario y generamos nuestro propio token JWT
            Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), null, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(authentication);

            return ResponseEntity.ok(new AuthResponse(jwt));

        } else {
            return new ResponseEntity<>("Token de Google inválido", HttpStatus.UNAUTHORIZED);
        }
    }
}
