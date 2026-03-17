package com.apoflow.backend.service;

import com.apoflow.backend.api.dto.StudentResponse;
import com.apoflow.backend.domain.Student;
import com.apoflow.backend.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@SuppressWarnings("null")
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<StudentResponse> findAll() {
        return studentRepository.findAll().stream()
                .sorted(Comparator.comparing(Student::getNome))
                .map(this::map)
                .toList();
    }

    public Student getById(String id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Aluno nao encontrado."));
    }

    public void increasePoints(String studentId, int points) {
        Student student = getById(studentId);
        student.setPontosAcumulados(student.getPontosAcumulados() + points);
        studentRepository.save(student);
    }

    public StudentResponse map(Student student) {
        return new StudentResponse(student.getId(), student.getNome(), student.getOrientadorId(), student.getPontosAcumulados());
    }
}
