package com.school.resultportal.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class PdfService {

    @Value("${pdf.upload.path}")
    private String uploadPath;

    public String savePdf(MultipartFile file, String filename) throws IOException {
        File directory = new File(uploadPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String fullPath = uploadPath + File.separator + filename;
        file.transferTo(new File(fullPath));

        return filename;
    }

    public File getPdf(String filename) {
        return new File(uploadPath + File.separator + filename);
    }
}
