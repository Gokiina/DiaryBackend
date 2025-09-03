package com.app.Diary.controller;

import com.app.Diary.model.User;
import com.app.Diary.payload.GoogleTokenRequest;
import com.app.Diary.payload.LoginRequest;
import com.app.Diary.payload.LoginResponse;
import com.app.Diary.payload.SignUpRequest;
import com.app.Diary.repository.UserRepository;
import com.app.Diary.security.JwtTokenProvider;

// Imports para Google
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // AÑADIDO: Logger para depuración
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

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
        // ... (sin cambios en este método)
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return ResponseEntity.ok(new LoginResponse(jwt, user));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignUpRequest signUpRequest) {
        // ... (sin cambios en este método)
        if (userRepository.findByEmail(signUpRequest.getEmail()).isPresent()) {
            return new ResponseEntity<>("¡El correo electrónico ya está en uso!", HttpStatus.BAD_REQUEST);
        }
        User user = new User();
        user.setName(signUpRequest.getName());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setProvider(com.app.Diary.model.AuthProvider.local);
        userRepository.save(user);
        return new ResponseEntity<>("¡Usuario registrado exitosamente!", HttpStatus.CREATED);
    }

    @PostMapping("/google")
    public ResponseEntity<?> authenticateUserWithGoogle(@RequestBody GoogleTokenRequest googleTokenRequest) {
        // AÑADIDO: Logs de depuración detallados
        logger.info("Recibida petición de autenticación con Google.");
        logger.debug("Client ID que se usará para la verificación: {}", googleClientId);

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        GoogleIdToken idToken;
        try {
            logger.info("Intentando verificar el token de Google...");
            idToken = verifier.verify(googleTokenRequest.getToken());
        } catch (GeneralSecurityException | IOException e) {
            logger.error("La verificación del token de Google falló.", e);
            return new ResponseEntity<>("Token de Google inválido: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
        }

        if (idToken == null) {
            logger.warn("Token de Google inválido (resultado nulo) después de la verificación.");
            return new ResponseEntity<>("Token de Google inválido", HttpStatus.UNAUTHORIZED);
        }

        logger.info("Verificación del token de Google exitosa.");

        GoogleIdToken.Payload payload = idToken.getPayload();
        String email = payload.getEmail();
        String name = (String) payload.get("name");

        logger.info("Procesando usuario con email: {}", email);

        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
            logger.info("Usuario encontrado en la base de datos: {}", user.getId());
        } else {
            logger.info("Usuario no encontrado. Creando nuevo usuario en la base de datos.");
            user = new User();
            user.setEmail(email);
            user.setName(name);
            user.setProvider(com.app.Diary.model.AuthProvider.google);
            user = userRepository.save(user);
            logger.info("Nuevo usuario creado con ID: {}", user.getId());
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        logger.info("Token JWT generado para el usuario. Devolviendo respuesta exitosa.");
        return ResponseEntity.ok(new LoginResponse(jwt, user));
    }
}
