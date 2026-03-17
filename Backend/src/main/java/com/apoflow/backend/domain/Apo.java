package com.apoflow.backend.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "apos")
public class Apo {

    @Id
    private String id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false, length = 2000)
    private String descricao;

    @Column(nullable = false)
    private Integer pontos;

    @Column(nullable = false)
    private String alunoId;

    @Column(nullable = false)
    private String aluno;

    @Column(nullable = false)
    private String orientadorId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApoStatus status;

    @Column(nullable = false)
    private LocalDate dataAtualizacao;

    @OneToMany(mappedBy = "apo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("id asc")
    private List<ApoAttachment> anexos = new ArrayList<>();

    @OneToMany(mappedBy = "apo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("id asc")
    private List<ApoVote> votos = new ArrayList<>();

    public Apo() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getPontos() {
        return pontos;
    }

    public void setPontos(Integer pontos) {
        this.pontos = pontos;
    }

    public String getAlunoId() {
        return alunoId;
    }

    public void setAlunoId(String alunoId) {
        this.alunoId = alunoId;
    }

    public String getAluno() {
        return aluno;
    }

    public void setAluno(String aluno) {
        this.aluno = aluno;
    }

    public String getOrientadorId() {
        return orientadorId;
    }

    public void setOrientadorId(String orientadorId) {
        this.orientadorId = orientadorId;
    }

    public ApoStatus getStatus() {
        return status;
    }

    public void setStatus(ApoStatus status) {
        this.status = status;
    }

    public LocalDate getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDate dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    public List<ApoAttachment> getAnexos() {
        return anexos;
    }

    public void setAnexos(List<ApoAttachment> anexos) {
        this.anexos = anexos;
    }

    public List<ApoVote> getVotos() {
        return votos;
    }

    public void setVotos(List<ApoVote> votos) {
        this.votos = votos;
    }
}
