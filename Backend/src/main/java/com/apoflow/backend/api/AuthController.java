package com.apoflow.backend.api;

import com.apoflow.backend.api.dto.LoginRequest;
import com.apoflow.backend.api.dto.AuthResponse;
import com.apoflow.backend.service.AuthService;
import com.apoflow.backend.service.TwoFactorService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apoflow.backend.api.dto.RegisterRequest;
import com.apoflow.backend.api.dto.ChangePasswordRequest;
import com.apoflow.backend.api.dto.AuthResponse;
import com.apoflow.backend.domain.Role;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.apoflow.backend.repository.AppUserRepository;
import com.apoflow.backend.security.JwtTokenProvider;
import com.apoflow.backend.domain.AppUser;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:80"})
public class AuthController {

    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;
    private final AppUserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final TwoFactorService twoFactorService;

    public AuthController(AuthService authService, PasswordEncoder passwordEncoder,
                         AppUserRepository userRepository, JwtTokenProvider jwtTokenProvider,
                         TwoFactorService twoFactorService) {
        this.authService = authService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.twoFactorService = twoFactorService;
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.loginByCredentials(request.email(), request.senha());
    }

    @PostMapping("/verify-otp")
    public AuthResponse verifyOtp(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String code = body.get("code");
        if (email == null || code == null) {
            throw new IllegalArgumentException("E-mail e código são obrigatórios.");
        }
        return twoFactorService.verifyOtp(email.trim(), code.trim());
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        if (userRepository.findByEmailIgnoreCase(registerRequest.email()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "E-mail já cadastrado. Tente outro ou faça login."));
        }

        Role papel;
        try {
            papel = Role.valueOf(registerRequest.papel().toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Perfil inválido: " + registerRequest.papel()));
        }
        AppUser newUser = new AppUser();
        newUser.setNome(registerRequest.nome());
        newUser.setEmail(registerRequest.email());
        newUser.setSenhaHash(passwordEncoder.encode(registerRequest.senha()));
        newUser.setPapel(papel);
        newUser.setPrimeiroAcesso(false);
        newUser.setRequerMudancaSenha(false);
        newUser.setHabilitado(true);
        newUser.setContaNaoExpirada(true);
        newUser.setContaNaoBloqueada(true);
        newUser.setCredenciaisNaoExpiradas(true);
        newUser.setCriadoEm(LocalDateTime.now());
        newUser.setAtualizadoEm(LocalDateTime.now());

        userRepository.save(newUser);

        String token = jwtTokenProvider.generateToken(registerRequest.email());

        return ResponseEntity.ok(new AuthResponse(
                token,
                newUser.getId(),
                newUser.getEmail(),
                newUser.getNome(),
                newUser.getPapel().name().toLowerCase(),
                false,
                "Usuário registrado com sucesso"
        ));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        Optional<AppUser> userOpt = userRepository.findByEmailIgnoreCase(changePasswordRequest.email());

        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Usuário não encontrado"));
        }

        AppUser user = userOpt.get();

        if (!user.isPrimeiroAcesso()) {
            if (changePasswordRequest.senhaAntiga() == null || 
                !passwordEncoder.matches(changePasswordRequest.senhaAntiga(), user.getSenhaHash())) {
                return ResponseEntity.badRequest().body(Map.of("message", "Senha atual incorreta"));
            }
        }

        user.setSenhaHash(passwordEncoder.encode(changePasswordRequest.novaSenha()));
        user.setPrimeiroAcesso(false);
        user.setRequerMudancaSenha(false);
        user.setUltimaMudancaSenha(LocalDateTime.now());
        user.setAtualizadoEm(LocalDateTime.now());

        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Senha alterada com sucesso"));
    }

    @GetMapping("/first-access/{email}")
    public ResponseEntity<?> checkFirstAccess(@PathVariable String email) {
        Optional<AppUser> userOpt = userRepository.findByEmailIgnoreCase(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Usuário não encontrado"));
        }

        AppUser user = userOpt.get();
        return ResponseEntity.ok(user.isPrimeiroAcesso());
    }
}
