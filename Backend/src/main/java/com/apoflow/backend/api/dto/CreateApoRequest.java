package com.apoflow.backend.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateApoRequest(
        @NotBlank String alunoId,
        @NotBlank String titulo,
        @NotBlank String tipo,
        @NotBlank String descricao,
        @Min(1) @Max(6) Integer pontos,
        @NotEmpty List<String> anexos
) {
}
