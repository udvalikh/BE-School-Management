package com.javacorner.admin.service;

import com.javacorner.admin.dto.StudentDTO;
import com.javacorner.admin.entity.Student;
import org.springframework.data.domain.Page;

public interface StudentService {

    Student loadStudentById(Long studentId);
    Page<StudentDTO> findStudentsByName(String name, int page, int size);
    StudentDTO findStudentByEmail(String email);
    StudentDTO createStudent(StudentDTO studentDto);
    StudentDTO updateStudent(StudentDTO studentDto);
    void removeStudent(Long studentId);
}
