package com.apoflow.backend.api;

import com.apoflow.backend.api.dto.ApoResponse;
import com.apoflow.backend.api.dto.CreateApoRequest;
import com.apoflow.backend.api.dto.DecisionRequest;
import com.apoflow.backend.api.dto.VoteRequest;
import com.apoflow.backend.service.ApoService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/apos")
public class ApoController {

    private final ApoService apoService;

    public ApoController(ApoService apoService) {
        this.apoService = apoService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<ApoResponse> findAll() {
        return apoService.findAll();
    }

    @PostMapping
    @PreAuthorize("hasRole('ALUNO')")
    public ApoResponse create(@Valid @RequestBody CreateApoRequest request) {
        return apoService.create(request);
    }

    @PostMapping("/rascunho")
    @PreAuthorize("hasRole('ALUNO')")
    public ApoResponse saveDraft(@Valid @RequestBody CreateApoRequest request) {
        return apoService.saveDraft(request);
    }

    @PutMapping("/{apoId}/aluno/reenviar")
    @PreAuthorize("hasRole('ALUNO')")
    public ApoResponse resubmitByAluno(@PathVariable String apoId, @Valid @RequestBody CreateApoRequest request) {
        return apoService.resubmitByAluno(apoId, request);
    }

    @PostMapping("/{apoId}/aluno/desistir")
    @PreAuthorize("hasRole('ALUNO')")
    public ApoResponse desistByAluno(@PathVariable String apoId) {
        return apoService.giveUpByAluno(apoId);
    }

    @PostMapping("/{apoId}/orientador/aprovar")
    @PreAuthorize("hasRole('ORIENTADOR')")
    public ApoResponse approveByOrientador(@PathVariable String apoId) {
        return apoService.approveByOrientador(apoId);
    }

    @PostMapping("/{apoId}/orientador/devolver")
    @PreAuthorize("hasRole('ORIENTADOR')")
    public ApoResponse returnByOrientador(@PathVariable String apoId, @Valid @RequestBody DecisionRequest request) {
        return apoService.returnByOrientador(apoId, request);
    }

    @PostMapping("/{apoId}/comissao/voto")
    @PreAuthorize("hasRole('COMISSAO')")
    public ApoResponse vote(@PathVariable String apoId, @Valid @RequestBody VoteRequest request) {
        return apoService.vote(apoId, request);
    }

    @PostMapping("/{apoId}/coordenacao/decisao")
    @PreAuthorize("hasRole('COORDENACAO')")
    public ApoResponse decide(@PathVariable String apoId, @RequestParam String action, @Valid @RequestBody DecisionRequest request) {
        return apoService.decideByCoordenacao(apoId, action, request);
    }

    @PostMapping("/{apoId}/secretaria/arquivar")
    @PreAuthorize("hasRole('SECRETARIA')")
    public ApoResponse archive(@PathVariable String apoId) {
        return apoService.archive(apoId);
    }

    @PostMapping("/{apoId}/secretaria/lancar")
    @PreAuthorize("hasRole('SECRETARIA')")
    public ApoResponse launch(@PathVariable String apoId) {
        return apoService.launch(apoId);
    }
}
