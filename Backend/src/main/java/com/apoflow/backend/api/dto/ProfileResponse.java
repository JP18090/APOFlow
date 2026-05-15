package com.apoflow.backend.api.dto;

public record ProfileResponse(
        String id,
        String nome,
        String email,
        String papel,
        String ra,
        String fotoUrl,
        String curso,
        Integer semestre,
        String periodo,
        String drt
) {
}
