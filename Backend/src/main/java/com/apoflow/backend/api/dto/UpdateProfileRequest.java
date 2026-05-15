package com.apoflow.backend.api.dto;

public record UpdateProfileRequest(
        String nome,
        String ra,
        String fotoUrl,
        String curso,
        Integer semestre,
        String periodo,
        String drt
) {
}
