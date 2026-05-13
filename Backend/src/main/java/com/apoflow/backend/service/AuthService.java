package com.apoflow.backend.service;

import com.apoflow.backend.api.dto.AuthResponse;
import com.apoflow.backend.domain.AppUser;
import com.apoflow.backend.repository.AppUserRepository;
import com.apoflow.backend.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Validates credentials and initiates the 2FA OTP flow.
 * Returns a pending response; the actual JWT is only issued after OTP verification.
 */
@Service
public class AuthService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final TwoFactorService twoFactorService;

    public AuthService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider, TwoFactorService twoFactorService) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.twoFactorService = twoFactorService;
    }

    /**
     * Phase 1: validate credentials, send OTP.
     * Returns AuthResponse with token=null and otpRequired=true.
     */
    public AuthResponse loginByCredentials(String email, String senha) {
        AppUser user = appUserRepository.findByEmailIgnoreCase(email.trim())
                .orElseThrow(() -> new IllegalArgumentException("Credenciais invalidas."));

        if (!passwordEncoder.matches(senha, user.getSenhaHash())) {
            throw new IllegalArgumentException("Credenciais invalidas.");
        }

        if (!user.isHabilitado() || !user.isContaNaoBloqueada() || !user.isContaNaoExpirada()) {
            throw new IllegalArgumentException("Usuario inativo ou bloqueado.");
        }

        twoFactorService.initiateOtp(user);

        // No token yet – frontend must submit the OTP to complete login.
        return new AuthResponse(null, user.getId(), user.getEmail(), user.getNome(),
                user.getPapel().name().toLowerCase(), user.isPrimeiroAcesso(), "OTP_REQUIRED");
    }
}
