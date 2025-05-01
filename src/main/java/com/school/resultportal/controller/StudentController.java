package com.school.resultportal.controller;

import com.school.resultportal.model.Student;
import com.school.resultportal.service.StudentService;
import com.school.resultportal.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/student")
@CrossOrigin(origins = "*")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private PdfService pdfService;

    // ✅ Add student
    @PostMapping("/add")
    public ResponseEntity<Student> addStudent(@RequestBody Student student) {
        return ResponseEntity.ok(studentService.addStudent(student));
    }

    // ✅ View single student by UID and Grade
    @GetMapping("/view")
    public ResponseEntity<Student> getStudent(@RequestParam String uid, @RequestParam String grade) {
        Optional<Student> result = studentService.getByUidAndGrade(uid, grade);
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ View all students
    @GetMapping("/all")
    public ResponseEntity<List<Student>> getAll() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    // ✅ Update student
    @PutMapping("/update")
    public ResponseEntity<Student> updateStudent(@RequestBody Student student) {
        return ResponseEntity.ok(studentService.updateStudent(student));
    }

    // ✅ Delete student
    @DeleteMapping("/delete/{uid}")
    public ResponseEntity<String> deleteStudent(@PathVariable String uid) {
        studentService.deleteStudent(uid);
        return ResponseEntity.ok("Student deleted successfully.");
    }

    // ✅ Upload PDF for a specific UID
    @PostMapping("/upload/{uid}")
    public ResponseEntity<String> uploadPdf(@PathVariable String uid, @RequestParam("file") MultipartFile file) {
        try {
            Optional<Student> optionalStudent = studentService.getByUid(uid);
            if (optionalStudent.isEmpty()) return ResponseEntity.notFound().build();

            String filename = uid + "_" + file.getOriginalFilename();
            pdfService.savePdf(file, filename);

            Student student = optionalStudent.get();
            student.setPdfFilename(filename);
            studentService.updateStudent(student);

            return ResponseEntity.ok("PDF uploaded successfully.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Upload failed: " + e.getMessage());
        }
    }

    // ✅ Serve PDF file for viewing/downloading
    @GetMapping("/pdf/{filename}")
    public ResponseEntity<?> getPdf(@PathVariable String filename) {
        File file = pdfService.getPdf(filename);
        if (!file.exists()) return ResponseEntity.notFound().build();

        return ResponseEntity.ok()
                .header("Content-Disposition", "inline; filename=" + filename)
                .body(new FileSystemResource(file));
    }

    // ✅ Bulk upload from server folder (e.g., uploads/std3-bulk)
    @PostMapping("/upload-bulk")
    public ResponseEntity<?> uploadBulk(@RequestParam("grade") String grade) {
        File folder = new File("uploads/std3-bulk");
        if (!folder.exists() || !folder.isDirectory()) {
            return ResponseEntity.badRequest().body("❌ Folder not found or empty.");
        }

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".pdf"));
        if (files == null || files.length == 0) {
            return ResponseEntity.badRequest().body("❌ No PDF files found.");
        }

        for (File file : files) {
            String uid = file.getName().replace(".pdf", "");
            try {
                Student student = new Student();
                student.setUid(uid);
                student.setGrade(grade);
                student.setPdfFilename(file.getName());

                studentService.addStudent(student);

                // Copy file to uploads/
                Path dest = Paths.get("uploads", file.getName());
                Files.copy(file.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);

            } catch (Exception e) {
                return ResponseEntity.internalServerError().body("❌ Error: " + file.getName());
            }
        }

        return ResponseEntity.ok("✅ Uploaded " + files.length + " PDFs for grade " + grade);
    }
}
