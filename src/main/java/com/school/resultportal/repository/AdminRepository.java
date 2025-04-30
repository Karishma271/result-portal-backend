package com.school.resultportal.repository;

import com.school.resultportal.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, String> {
    Admin findByUsername(String username);
}
