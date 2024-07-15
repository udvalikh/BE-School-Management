package com.javacorner.admin.mapper;

import com.javacorner.admin.dto.StudentDTO;
import com.javacorner.admin.entity.Student;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class StudentMapper {

    public StudentDTO fromStudent(Student student){
        StudentDTO studentDto= new StudentDTO();
        BeanUtils.copyProperties(student, studentDto);
        return studentDto;
    }

    public Student fromStudentDto(StudentDTO studentDto) {
        Student student = new Student();
        BeanUtils.copyProperties(studentDto, student);
        return student;
    }
}
