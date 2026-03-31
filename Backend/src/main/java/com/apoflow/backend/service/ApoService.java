package com.apoflow.backend.service;

import com.apoflow.backend.api.dto.ApoResponse;
import com.apoflow.backend.api.dto.ApoVoteResponse;
import com.apoflow.backend.api.dto.CreateApoRequest;
import com.apoflow.backend.api.dto.DecisionRequest;
import com.apoflow.backend.api.dto.VoteRequest;
import com.apoflow.backend.domain.Apo;
import com.apoflow.backend.domain.ApoAttachment;
import com.apoflow.backend.domain.ApoStatus;
import com.apoflow.backend.domain.ApoVote;
import com.apoflow.backend.domain.Student;
import com.apoflow.backend.domain.VoteDecision;
import com.apoflow.backend.repository.ApoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@SuppressWarnings("null")
public class ApoService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final ApoRepository apoRepository;
    private final StudentService studentService;
    private final NotificationService notificationService;

    public ApoService(ApoRepository apoRepository, StudentService studentService, NotificationService notificationService) {
        this.apoRepository = apoRepository;
        this.studentService = studentService;
        this.notificationService = notificationService;
    }

    public List<ApoResponse> findAll() {
        return apoRepository.findAll().stream()
                .sorted(Comparator.comparing(Apo::getDataAtualizacao).reversed())
                .map(this::map)
                .toList();
    }

    @Transactional
    public ApoResponse create(CreateApoRequest request) {
        Student student = studentService.getById(request.alunoId());

        if (request.anexos().isEmpty()) {
            throw new IllegalArgumentException("Adicione pelo menos um anexo para submeter a APO.");
        }

        Apo apo = new Apo();
        apo.setId("apo-" + UUID.randomUUID().toString().substring(0, 8));
        applyRequestData(apo, request);
        apo.setAlunoId(student.getId());
        apo.setAluno(student.getNome());
        apo.setOrientadorId(student.getOrientadorId());
        apo.setStatus(ApoStatus.EM_AVALIACAO_ORIENTADOR);
        apo.setDataAtualizacao(LocalDate.now());

        Apo saved = apoRepository.save(apo);
        notificationService.create(
                "noti-" + UUID.randomUUID().toString().substring(0, 8),
                "Nova submissao de " + student.getNome() + " aguardando avaliacao do orientador",
                "Agora mesmo",
                false,
                "orientador"
        );
        return map(saved);
    }

    @Transactional
    public ApoResponse saveDraft(CreateApoRequest request) {
        Student student = studentService.getById(request.alunoId());

        Apo apo = new Apo();
        apo.setId("apo-" + UUID.randomUUID().toString().substring(0, 8));
        applyRequestData(apo, request);
        apo.setAlunoId(student.getId());
        apo.setAluno(student.getNome());
        apo.setOrientadorId(student.getOrientadorId());
        apo.setStatus(ApoStatus.RASCUNHO);
        apo.setDataAtualizacao(LocalDate.now());

        return map(apoRepository.save(apo));
    }

    @Transactional
    public ApoResponse resubmitByAluno(String apoId, CreateApoRequest request) {
        if (request.anexos().isEmpty()) {
            throw new IllegalArgumentException("Adicione pelo menos um anexo para reenviar a APO.");
        }

        Apo apo = getEntity(apoId);
        if (apo.getStatus() != ApoStatus.DEVOLVIDA && apo.getStatus() != ApoStatus.RASCUNHO) {
            throw new IllegalArgumentException("Apenas APO devolvida ou rascunho pode ser editada e reenviada.");
        }

        if (!apo.getAlunoId().equals(request.alunoId())) {
            throw new IllegalArgumentException("Aluno invalido para esta APO.");
        }

        applyRequestData(apo, request);
        apo.getVotos().clear();
        apo.setStatus(ApoStatus.EM_AVALIACAO_ORIENTADOR);
        apo.setDataAtualizacao(LocalDate.now());

        notificationService.create(
                id("noti"),
                "APO reenviada por " + apo.getAluno() + " aguardando avaliacao do orientador",
                "Agora mesmo",
                false,
                "orientador"
        );

        return map(apoRepository.save(apo));
    }

    @Transactional
    public ApoResponse giveUpByAluno(String apoId) {
        Apo apo = getEntity(apoId);
        if (apo.getStatus() != ApoStatus.DEVOLVIDA && apo.getStatus() != ApoStatus.RASCUNHO) {
            throw new IllegalArgumentException("Somente APO devolvida ou rascunho pode ser desistida.");
        }

        apo.setStatus(ApoStatus.DESISTIDA);
        apo.setDataAtualizacao(LocalDate.now());
        notificationService.create(id("noti"), "O aluno desistiu da APO \"" + apo.getTitulo() + "\"", "Agora mesmo", false, "orientador");
        return map(apoRepository.save(apo));
    }

    @Transactional
    public ApoResponse approveByOrientador(String apoId) {
        Apo apo = getEntity(apoId);
        apo.setStatus(ApoStatus.EM_AVALIACAO_COMISSAO);
        apo.setDataAtualizacao(LocalDate.now());
        notificationService.create(id("noti"), "APO \"" + apo.getTitulo() + "\" enviada para a comissao", "Agora mesmo", false, "comissao");
        return map(apoRepository.save(apo));
    }

    @Transactional
    public ApoResponse returnByOrientador(String apoId, DecisionRequest request) {
        Apo apo = getEntity(apoId);
        apo.setStatus(ApoStatus.DEVOLVIDA);
        apo.setDataAtualizacao(LocalDate.now());
        notificationService.create(id("noti"), "Sua APO \"" + apo.getTitulo() + "\" foi devolvida pelo orientador: " + request.justificativa(), "Agora mesmo", false, "aluno");
        return map(apoRepository.save(apo));
    }

    @Transactional
    public ApoResponse vote(String apoId, VoteRequest request) {
        Apo apo = getEntity(apoId);
        String justificativa = request.justificativa() == null ? "" : request.justificativa().trim();
        if (justificativa.isEmpty()) {
            throw new IllegalArgumentException("Justificativa obrigatoria para voto da comissao.");
        }

        String membro = request.membro().trim();
        VoteDecision decision = VoteDecision.valueOf(request.decisao().trim().toUpperCase());
        apo.getVotos().removeIf(existing -> existing.getMembro().equalsIgnoreCase(membro));
        apo.getVotos().add(new ApoVote(apo, membro, decision, justificativa));
        apo.setDataAtualizacao(LocalDate.now());

        if (decision == VoteDecision.DEVOLVER) {
            apo.setStatus(ApoStatus.EM_AVALIACAO_ORIENTADOR);
        } else if (apo.getVotos().stream().filter(v -> v.getDecisao() == VoteDecision.APROVAR).count() >= 2) {
            apo.setStatus(ApoStatus.EM_AVALIACAO_COORDENACAO);
            notificationService.create(id("noti"), "APO \"" + apo.getTitulo() + "\" encaminhada para a coordenacao", "Agora mesmo", false, "coordenacao");
        }

        return map(apoRepository.save(apo));
    }

    @Transactional
    public ApoResponse decideByCoordenacao(String apoId, String decision, DecisionRequest request) {
        Apo apo = getEntity(apoId);
        String normalized = decision.trim().toLowerCase();
        apo.setDataAtualizacao(LocalDate.now());

        switch (normalized) {
            case "aprovar" -> {
                apo.setStatus(ApoStatus.APROVADO);
                notificationService.create(id("noti"), "APO \"" + apo.getTitulo() + "\" aprovada pela coordenacao", "Agora mesmo", false, "secretaria");
            }
            case "reprovar" -> apo.setStatus(ApoStatus.REPROVADO);
            case "devolver" -> apo.setStatus(ApoStatus.EM_AVALIACAO_COMISSAO);
            default -> throw new IllegalArgumentException("Decisao de coordenacao invalida.");
        }

        notificationService.create(id("noti"), "Atualizacao na APO \"" + apo.getTitulo() + "\": " + request.justificativa(), "Agora mesmo", false, "aluno");
        return map(apoRepository.save(apo));
    }

    @Transactional
    public ApoResponse archive(String apoId) {
        Apo apo = getEntity(apoId);
        apo.setStatus(ApoStatus.ARQUIVADO);
        apo.setDataAtualizacao(LocalDate.now());
        return map(apoRepository.save(apo));
    }

    @Transactional
    public ApoResponse launch(String apoId) {
        Apo apo = getEntity(apoId);
        apo.setStatus(ApoStatus.LANCADO);
        apo.setDataAtualizacao(LocalDate.now());
        notificationService.create(id("noti"), "Credito da APO \"" + apo.getTitulo() + "\" lancado no sistema academico", "Agora mesmo", false, "aluno");
        return map(apoRepository.save(apo));
    }

    public Apo getEntity(String apoId) {
        return apoRepository.findById(apoId)
                .orElseThrow(() -> new IllegalArgumentException("APO nao encontrada."));
    }

    public ApoResponse map(Apo apo) {
        return new ApoResponse(
                apo.getId(),
                apo.getTitulo(),
                apo.getTipo(),
                apo.getDescricao(),
                apo.getPontos(),
                apo.getAlunoId(),
                apo.getAluno(),
                apo.getOrientadorId(),
                apo.getStatus().name().toLowerCase(),
                apo.getAnexos().stream().map(ApoAttachment::getNomeArquivo).toList(),
                apo.getDataAtualizacao().format(DATE_FORMATTER),
                apo.getVotos().stream()
                        .map(voto -> new ApoVoteResponse(voto.getMembro(), voto.getDecisao().name().toLowerCase(), voto.getJustificativa()))
                        .toList()
        );
    }

    private String id(String prefix) {
        return prefix + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private void applyRequestData(Apo apo, CreateApoRequest request) {
        apo.setTitulo(request.titulo());
        apo.setTipo(request.tipo());
        apo.setDescricao(request.descricao());
        apo.setPontos(request.pontos());
        apo.getAnexos().clear();
        request.anexos().forEach(name -> apo.getAnexos().add(new ApoAttachment(apo, name)));
    }
}
