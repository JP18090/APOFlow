package com.apoflow.backend.service;

import com.apoflow.backend.api.dto.UserResponse;
import com.apoflow.backend.domain.AppUser;
import com.apoflow.backend.repository.AppUserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AppUserRepository appUserRepository;

    public AuthService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    public UserResponse loginByCredentials(String email, String senha) {
        AppUser user = appUserRepository.findByEmailIgnoreCaseAndSenha(email.trim(), senha)
                .orElseThrow(() -> new IllegalArgumentException("Credenciais invalidas."));
        return mapUser(user);
    }

    public UserResponse mapUser(AppUser user) {
        return new UserResponse(user.getId(), user.getNome(), user.getEmail(), user.getPapel().name().toLowerCase());
    }
}
