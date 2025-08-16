package com.app.Diary.model; // Asegúrate de que el paquete sea correcto

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

@Document(collection = "users") // Esto le dice a MongoDB que guarde los usuarios en una colección llamada "users"
public class User {

    @Id
    private String id;

    @Indexed(unique = true) // Asegura que no puede haber dos usuarios con el mismo email
    private String email;

    private String name;

    private String password; // Este campo lo usaremos si implementamos registro con contraseña

    private AuthProvider provider; // Para saber si se registró con "google" o "local"

    private boolean isPremium = false; // Por defecto, los usuarios no son premium

    // --- Getters y Setters ---
    // (Puedes generarlos automáticamente en IntelliJ con clic derecho -> Generate -> Getters and Setters)

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public AuthProvider getProvider() {
        return provider;
    }

    public void setProvider(AuthProvider provider) {
        this.provider = provider;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }
}