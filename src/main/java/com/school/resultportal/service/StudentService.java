package com.school.resultportal.service;

import com.school.resultportal.model.Student;
import com.school.resultportal.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.List;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    public Student addStudent(Student student) {
        return studentRepository.save(student);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Optional<Student> getByUidAndGrade(String uid, String grade) {
        return studentRepository.findByUidAndGrade(uid, grade);
    }

    public void deleteStudent(String uid) {
        studentRepository.deleteById(uid);
    }

    public Student updateStudent(Student student) {
        return studentRepository.save(student);
    }
}
