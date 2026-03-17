package com.apoflow.backend.repository;

import com.apoflow.backend.domain.Apo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApoRepository extends JpaRepository<Apo, String> {
    List<Apo> findByAlunoId(String alunoId);
}
