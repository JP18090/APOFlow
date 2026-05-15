package com.apoflow.backend.api;

import com.apoflow.backend.api.dto.ProfileResponse;
import com.apoflow.backend.api.dto.UpdateProfileRequest;
import com.apoflow.backend.domain.AppUser;
import com.apoflow.backend.repository.AppUserRepository;
import com.apoflow.backend.repository.ApoRepository;
import com.apoflow.backend.repository.StudentRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final AppUserRepository userRepository;
    private final StudentRepository studentRepository;
    private final ApoRepository apoRepository;

    public UserController(AppUserRepository userRepository,
                          StudentRepository studentRepository,
                          ApoRepository apoRepository) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.apoRepository = apoRepository;
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ProfileResponse getMe(@AuthenticationPrincipal UserDetails principal) {
        AppUser user = findUser(principal);
        return toResponse(user);
    }

    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ProfileResponse updateMe(@AuthenticationPrincipal UserDetails principal,
                                    @RequestBody UpdateProfileRequest request) {
        AppUser user = findUser(principal);

        if (request.nome() != null && !request.nome().isBlank()) {
            user.setNome(request.nome());
        }
        if (request.fotoUrl() != null) user.setFotoUrl(request.fotoUrl());
        if (request.ra() != null) user.setRa(request.ra());
        if (request.curso() != null) user.setCurso(request.curso());
        if (request.semestre() != null) user.setSemestre(request.semestre());
        if (request.periodo() != null) user.setPeriodo(request.periodo());
        if (request.drt() != null) user.setDrt(request.drt());
        user.setAtualizadoEm(LocalDateTime.now());

        userRepository.save(user);
        return toResponse(user);
    }

    @DeleteMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> deleteMe(@AuthenticationPrincipal UserDetails principal) {
        AppUser user = findUser(principal);
        apoRepository.deleteByAlunoId(user.getId());
        studentRepository.deleteById(user.getId());
        userRepository.delete(user);
        return ResponseEntity.ok(Map.of("message", "Conta excluida com sucesso."));
    }

    private AppUser findUser(UserDetails principal) {
        return userRepository.findByEmailIgnoreCase(principal.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
    }

    private ProfileResponse toResponse(AppUser user) {
        return new ProfileResponse(
                user.getId(),
                user.getNome(),
                user.getEmail(),
                user.getPapel().name().toLowerCase(),
                user.getRa(),
                user.getFotoUrl(),
                user.getCurso(),
                user.getSemestre(),
                user.getPeriodo(),
                user.getDrt()
        );
    }
}
