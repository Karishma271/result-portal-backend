package com.school.resultportal.controller;

import com.school.resultportal.model.Student;
import com.school.resultportal.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.school.resultportal.service.PdfService;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/student")
@CrossOrigin(origins = "*")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @PostMapping("/add")
    public ResponseEntity<Student> addStudent(@RequestBody Student student) {
        return ResponseEntity.ok(studentService.addStudent(student));
    }

    @GetMapping("/view")
    public ResponseEntity<Student> getStudent(@RequestParam String uid, @RequestParam String grade) {
        Optional<Student> result = studentService.getByUidAndGrade(uid, grade);
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/all")
    public ResponseEntity<List<Student>> getAll() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @PutMapping("/update")
    public ResponseEntity<Student> updateStudent(@RequestBody Student student) {
        return ResponseEntity.ok(studentService.updateStudent(student));
    }

    @DeleteMapping("/delete/{uid}")
    public ResponseEntity<String> deleteStudent(@PathVariable String uid) {
        studentService.deleteStudent(uid);
        return ResponseEntity.ok("Student deleted successfully.");
    }

    @Autowired
    private PdfService pdfService;

    // ✅ Upload PDF for a student
    @PostMapping("/upload/{uid}")
    public ResponseEntity<String> uploadPdf(@PathVariable String uid, @RequestParam("file") MultipartFile file) {
        try {
            Student student = studentService.getByUidAndGrade(uid, "").orElse(null);
            if (student == null) return ResponseEntity.notFound().build();

            String filename = uid + "_" + file.getOriginalFilename();
            pdfService.savePdf(file, filename);
            student.setPdfFilename(filename);
            studentService.updateStudent(student);
            return ResponseEntity.ok("PDF uploaded successfully.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Upload failed: " + e.getMessage());
        }
    }

    // ✅ View or download PDF by filename
    @GetMapping("/pdf/{filename}")
    public ResponseEntity<?> getPdf(@PathVariable String filename) {
        File file = pdfService.getPdf(filename);
        if (!file.exists()) return ResponseEntity.notFound().build();

        return ResponseEntity.ok()
                .header("Content-Disposition", "inline; filename=" + filename)
                .body(org.springframework.core.io.FileSystemResource.class.cast(new org.springframework.core.io.FileSystemResource(file)));
    }
}
