package com.apoflow.backend.repository;

import com.apoflow.backend.domain.AppUser;
import com.apoflow.backend.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, String> {
    Optional<AppUser> findFirstByPapel(Role papel);
    Optional<AppUser> findByEmailIgnoreCaseAndSenha(String email, String senha);
}
