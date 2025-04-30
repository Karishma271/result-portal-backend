package com.school.resultportal.repository;

import com.school.resultportal.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, String> {
    Optional<Student> findByUidAndGrade(String uid, String grade);
}
