package com.apoflow.backend.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "apo_attachments")
public class ApoAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "apo_id")
    private Apo apo;

    private String nomeArquivo;

    public ApoAttachment() {
    }

    public ApoAttachment(Apo apo, String nomeArquivo) {
        this.apo = apo;
        this.nomeArquivo = nomeArquivo;
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

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }
}
