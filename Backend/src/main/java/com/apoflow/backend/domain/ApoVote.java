package com.apoflow.backend.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "apo_votes")
public class ApoVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "apo_id")
    private Apo apo;

    private String membro;

    @Enumerated(EnumType.STRING)
    private VoteDecision decisao;

    private String justificativa;

    public ApoVote() {
    }

    public ApoVote(Apo apo, String membro, VoteDecision decisao, String justificativa) {
        this.apo = apo;
        this.membro = membro;
        this.decisao = decisao;
        this.justificativa = justificativa;
    }

    public Long getId() {
        return id;
    }

    public Apo getApo() {
        return apo;
    }

    public void setApo(Apo apo) {
        this.apo = apo;
    }

    public String getMembro() {
        return membro;
    }

    public void setMembro(String membro) {
        this.membro = membro;
    }

    public VoteDecision getDecisao() {
        return decisao;
    }

    public void setDecisao(VoteDecision decisao) {
        this.decisao = decisao;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }
}
