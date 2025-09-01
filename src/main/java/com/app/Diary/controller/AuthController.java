package com.app.Diary.controller;

import com.app.Diary.model.AuthProvider;
import com.app.Diary.model.User;
import com.app.Diary.payload.GoogleTokenRequest;
import com.app.Diary.payload.LoginRequest;
import com.app.Diary.payload.LoginResponse; // MODIFICADO: Import para la nueva respuesta
import com.app.Diary.payload.SignUpRequest;
import com.app.Diary.repository.UserRepository;
import com.app.Diary.security.JwtTokenProvider;

// Imports para Google
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
import org.springframework.security.core.userdetails.UserDetails; // AÑADIDO: Para obtener info del usuario
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
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        // AÑADIDO: Obtener los detalles del usuario para devolverlos en la respuesta
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Error: Usuario no encontrado después de la autenticación."));

        // MODIFICADO: Devolver el token Y el usuario
        return ResponseEntity.ok(new LoginResponse(jwt, user));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignUpRequest signUpRequest) {
        if (userRepository.findByEmail(signUpRequest.getEmail()).isPresent()) {
            return new ResponseEntity<>("¡El correo electrónico ya está en uso!", HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setName(signUpRequest.getName());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setProvider(AuthProvider.local);

        userRepository.save(user);

        return new ResponseEntity<>("¡Usuario registrado exitosamente!", HttpStatus.CREATED);
    }

    @PostMapping("/google")
    public ResponseEntity<?> authenticateUserWithGoogle(@RequestBody GoogleTokenRequest googleTokenRequest) {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        GoogleIdToken idToken;
        try {
            idToken = verifier.verify(googleTokenRequest.getToken());
        } catch (Exception e) {
            return new ResponseEntity<>("Token de Google inválido: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
        }

        if (idToken == null) {
            return new ResponseEntity<>("Token de Google inválido o expirado.", HttpStatus.UNAUTHORIZED);
        }

        GoogleIdToken.Payload payload = idToken.getPayload();
        String email = payload.getEmail();
        String name = (String) payload.get("name");

        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
        } else {
            // Si el usuario no existe, lo creamos
            user = new User();
            user.setEmail(email);
            user.setName(name);
            user.setProvider(AuthProvider.google);
            // Al ser login con Google, no necesita password en nuestra DB
            user = userRepository.save(user);
        }

        // Creamos una autenticación para el usuario en nuestro sistema
        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generamos nuestro propio token JWT
        String jwt = tokenProvider.generateToken(authentication);

        // MODIFICADO: Devolver nuestro token JWT Y el objeto del usuario
        return ResponseEntity.ok(new LoginResponse(jwt, user));
    }
}