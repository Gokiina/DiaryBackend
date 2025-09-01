package com.app.Diary.payload;

import com.app.Diary.model.User;

/**
 * Representa la respuesta JSON que se envía al cliente tras un inicio de sesión exitoso.
 * Contiene el token de acceso JWT y el objeto completo del usuario.
 */
public class LoginResponse {

    private String accessToken;
    private User user;

    public LoginResponse(String accessToken, User user) {
        this.accessToken = accessToken;
        this.user = user;
    }

    // Getters y Setters

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}