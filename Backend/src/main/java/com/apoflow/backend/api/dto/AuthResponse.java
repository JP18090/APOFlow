package com.apoflow.backend.api.dto;

public record AuthResponse(
    String token,
    String userId,
    String email,
    String nome,
    String papel,
    boolean primeiroAcesso,
    String mensagem
) {
}
