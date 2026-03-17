package com.apoflow.backend.config;

import com.apoflow.backend.domain.Apo;
import com.apoflow.backend.domain.ApoAttachment;
import com.apoflow.backend.domain.ApoStatus;
import com.apoflow.backend.domain.ApoVote;
import com.apoflow.backend.domain.AppNotification;
import com.apoflow.backend.domain.AppUser;
import com.apoflow.backend.domain.Role;
import com.apoflow.backend.domain.Student;
import com.apoflow.backend.domain.VoteDecision;
import com.apoflow.backend.repository.ApoRepository;
import com.apoflow.backend.repository.AppNotificationRepository;
import com.apoflow.backend.repository.AppUserRepository;
import com.apoflow.backend.repository.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.List;

@Configuration
@SuppressWarnings("null")
public class DataInitializer {

    @Bean
    CommandLineRunner seedData(
            AppUserRepository userRepository,
            StudentRepository studentRepository,
            ApoRepository apoRepository,
            AppNotificationRepository notificationRepository
    ) {
        return args -> {
            if (userRepository.count() > 0) {
                return;
            }

            userRepository.saveAll(List.of(
                    new AppUser("aluno-1", "Jose Pedro Bitetti", "jose@apoflow.dev", Role.ALUNO),
                    new AppUser("orientador-1", "Prof. Gustavo Netto", "gustavo@apoflow.dev", Role.ORIENTADOR),
                    new AppUser("comissao-1", "Prof. Gabriel Labarca Del Bianco", "gabriel@apoflow.dev", Role.COMISSAO),
                    new AppUser("coordenacao-1", "Prof. Vitor Costa", "vitor@apoflow.dev", Role.COORDENACAO),
                    new AppUser("secretaria-1", "Dr. Luiz Batista dos Santos", "luiz@apoflow.dev", Role.SECRETARIA)
            ));

            studentRepository.saveAll(List.of(
                    new Student("aluno-1", "Jose Pedro Bitetti", "orientador-1", 10),
                    new Student("aluno-2", "Rodrygo Rogerio Vasconcellos", "orientador-1", 12),
                    new Student("aluno-3", "Gabriel Labarca Del Bianco", "orientador-1", 7)
            ));

            apoRepository.saveAll(List.of(
                    apo("apo-1", "Artigo publicado na IEEE Access", "Artigo em periodico", "Publicacao aceita em periodico internacional com comprovante de DOI e carta de aceite.", 4, "aluno-1", "Jose Pedro Bitetti", "orientador-1", ApoStatus.EM_AVALIACAO_ORIENTADOR, LocalDate.of(2026, 3, 15), List.of("doi.pdf", "aceite.pdf"), List.of()),
                    apo("apo-2", "Estagio de docencia em IA Aplicada", "Estagio docencia", "Atuacao em disciplina de graduacao com plano de aulas e relatorio final.", 3, "aluno-1", "Jose Pedro Bitetti", "orientador-1", ApoStatus.EM_AVALIACAO_COMISSAO, LocalDate.of(2026, 3, 14), List.of("relatorio.pdf", "plano.pdf"), List.of(
                            vote("Profa. Ana Ribeiro", VoteDecision.APROVAR, "Documentacao consistente e aderente ao regulamento."),
                            vote("Prof. Bruno Lima", VoteDecision.APROVAR, "Pontuacao adequada e evidencias completas.")
                    )),
                    apo("apo-3", "Capitulo Springer sobre visao computacional", "Capitulo de livro", "Capitulo publicado em coletanea internacional com metadados e comprovante editorial.", 5, "aluno-2", "Rodrygo Rogerio Vasconcellos", "orientador-1", ApoStatus.EM_AVALIACAO_COORDENACAO, LocalDate.of(2026, 3, 13), List.of("capitulo.pdf", "comprovante-editorial.pdf"), List.of(
                            vote("Profa. Ana Ribeiro", VoteDecision.APROVAR, "Atividade qualificada e com documentacao completa."),
                            vote("Prof. Bruno Lima", VoteDecision.REPROVAR, "Pontuacao sugerida acima da tabela."),
                            vote("Prof. Carlos Souza", VoteDecision.APROVAR, "Mantem aderencia a categoria prevista.")
                    )),
                    apo("apo-4", "Apresentacao em congresso de software livre", "Artigo em congresso", "Apresentacao oral com certificado e registro do evento.", 2, "aluno-3", "Gabriel Labarca Del Bianco", "orientador-1", ApoStatus.APROVADO, LocalDate.of(2026, 3, 10), List.of("certificado.pdf"), List.of()),
                    apo("apo-5", "Minicurso em aprendizado de maquina", "Minicurso ministrado", "Minicurso ministrado durante escola de verao com lista de presenca.", 2, "aluno-2", "Rodrygo Rogerio Vasconcellos", "orientador-1", ApoStatus.ARQUIVADO, LocalDate.of(2026, 3, 8), List.of("minicurso.pdf", "lista-presenca.pdf"), List.of()),
                    apo("apo-6", "Registro de software para laboratorio", "Patente ou software", "Registro institucional do software com comprovante emitido pela universidade.", 6, "aluno-2", "Rodrygo Rogerio Vasconcellos", "orientador-1", ApoStatus.LANCADO, LocalDate.of(2026, 3, 1), List.of("registro.pdf"), List.of()),
                    apo("apo-7", "Rascunho de participacao em banca", "Participacao em comissao", "Rascunho salvo aguardando anexos finais.", 1, "aluno-1", "Jose Pedro Bitetti", "orientador-1", ApoStatus.RASCUNHO, LocalDate.of(2026, 3, 16), List.of(), List.of()),
                    apo("apo-8", "Submissao com documentacao inconsistente", "Artigo em congresso", "Caso reprovado para alimentar a visualizacao historica do aluno.", 2, "aluno-1", "Jose Pedro Bitetti", "orientador-1", ApoStatus.REPROVADO, LocalDate.of(2026, 2, 22), List.of("submissao.pdf"), List.of())
            ));

            notificationRepository.saveAll(List.of(
                    new AppNotification("noti-1", "APO \"Artigo IEEE\" recebeu nova avaliacao", "2 horas atras", false, "aluno"),
                    new AppNotification("noti-2", "Sua APO \"Estagio Docencia\" foi aprovada pelo orientador", "1 dia atras", false, "aluno"),
                    new AppNotification("noti-3", "Nova submissao de Jose Pedro Bitetti aguardando avaliacao", "2 dias atras", true, "orientador"),
                    new AppNotification("noti-4", "Lembrete: 3 APOs pendentes de votacao na comissao", "3 dias atras", true, "comissao"),
                    new AppNotification("noti-5", "APO \"Capitulo Springer\" aprovada pela coordenacao", "1 semana atras", true, "coordenacao"),
                    new AppNotification("noti-6", "Pacote de arquivamento pronto para revisao", "1 dia atras", false, "secretaria")
            ));
        };
    }

    private static Apo apo(String id, String titulo, String tipo, String descricao, int pontos, String alunoId, String aluno, String orientadorId,
                           ApoStatus status, LocalDate dataAtualizacao, List<String> anexos, List<ApoVote> votos) {
        Apo apo = new Apo();
        apo.setId(id);
        apo.setTitulo(titulo);
        apo.setTipo(tipo);
        apo.setDescricao(descricao);
        apo.setPontos(pontos);
        apo.setAlunoId(alunoId);
        apo.setAluno(aluno);
        apo.setOrientadorId(orientadorId);
        apo.setStatus(status);
        apo.setDataAtualizacao(dataAtualizacao);
        anexos.forEach(anexo -> apo.getAnexos().add(new ApoAttachment(apo, anexo)));
        votos.forEach(voto -> {
            voto.setApo(apo);
            apo.getVotos().add(voto);
        });
        return apo;
    }

    private static ApoVote vote(String membro, VoteDecision decisao, String justificativa) {
        return new ApoVote(null, membro, decisao, justificativa);
    }
}
