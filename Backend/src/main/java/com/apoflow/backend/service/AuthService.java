package com.apoflow.backend.service;

import com.apoflow.backend.api.dto.UserResponse;
import com.apoflow.backend.domain.AppUser;
import com.apoflow.backend.domain.Role;
import com.apoflow.backend.repository.AppUserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AppUserRepository appUserRepository;

    public AuthService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    public UserResponse loginByRole(String roleValue) {
        Role role = Role.valueOf(roleValue.trim().toUpperCase());
        AppUser user = appUserRepository.findFirstByPapel(role)
                .orElseThrow(() -> new IllegalArgumentException("Papel nao encontrado."));
        return mapUser(user);
    }

    public UserResponse mapUser(AppUser user) {
        return new UserResponse(user.getId(), user.getNome(), user.getEmail(), user.getPapel().name().toLowerCase());
    }
}
