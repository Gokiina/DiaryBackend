package com.app.Diary.repository; // Asegúrate de que el paquete sea correcto

import com.app.Diary.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    /**
     * Busca un usuario por su dirección de correo electrónico.
     * Spring Data MongoDB implementará este método automáticamente.
     * @param email El email del usuario a buscar.
     * @return Un Optional que contiene al usuario si se encuentra, o vacío si no.
     */
    Optional<User> findByEmail(String email);

}
