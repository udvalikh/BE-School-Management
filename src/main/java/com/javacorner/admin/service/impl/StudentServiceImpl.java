package com.javacorner.admin.service.impl;


import com.javacorner.admin.dao.StudentDao;
import com.javacorner.admin.dto.StudentDTO;
import com.javacorner.admin.entity.Course;
import com.javacorner.admin.entity.Student;
import com.javacorner.admin.entity.User;
import com.javacorner.admin.mapper.StudentMapper;
import com.javacorner.admin.service.CourseService;
import com.javacorner.admin.service.StudentService;
import com.javacorner.admin.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.stream.Collectors;

@Service
@Transactional
public class StudentServiceImpl implements StudentService {

    private StudentDao studentDao;
    private StudentMapper studentMapper;
    private UserService userService;
    private CourseService courseService;

    public StudentServiceImpl(StudentDao studentDao, StudentMapper studentMapper, UserService userService, CourseService courseService) {
        this.studentDao = studentDao;
        this.studentMapper = studentMapper;
        this.userService = userService;
        this.courseService = courseService;
    }


    @Override
    public Student loadStudentById(Long studentId) {
        return studentDao.findById(studentId).orElseThrow(()-> new EntityNotFoundException("Student Id with: "+ studentId+" Not Found"));
    }

    @Override
    public Page<StudentDTO> findStudentsByName(String name, int page, int size) {
        PageRequest pageRequest=PageRequest.of(page, size);
        Page<Student> studentsPage= studentDao.findStudentsByName(name, pageRequest);
        return new PageImpl<>(studentsPage.getContent().stream().map(student-> studentMapper.fromStudent(student)).collect(Collectors.toList()), pageRequest, studentsPage.getTotalElements());

    }

    @Override
    public StudentDTO findStudentByEmail(String email) {
        return studentMapper.fromStudent(studentDao.findStudentByEmail(email));
    }

    @Override
    public StudentDTO createStudent(StudentDTO studentDto) {
        User user = userService.createUser(studentDto.getUser().getEmail(), studentDto.getUser().getPassword());
        userService.assignRoleToUser(user.getEmail(), "Student");
        Student student = studentMapper.fromStudentDto(studentDto);
        student.setUser(user);
        Student savedStudent = studentDao.save(student);
        return studentMapper.fromStudent(savedStudent);
//        studentDto.setStudentId(1L);
//        Student savedStudent = studentMapper.fromStudentDto(studentDto);
//        savedStudent= studentDao.save(savedStudent);
//        Student loadedStudent=loadStudentById(savedStudent.getStudentId());
//        User user=userService.createUser(studentDto.getUser().getEmail(), studentDto.getUser().getPassword());
//        userService.assignRoleToUser(user.getEmail(), "Student");
//        savedStudent.setUser(user);
//        savedStudent.setCourses(loadedStudent.getCourses());
//        studentDao.save(savedStudent);
//        return studentMapper.fromStudent(savedStudent);
    }

    @Override
    public StudentDTO updateStudent(StudentDTO studentDto) {
        Student loadedStudent= loadStudentById(studentDto.getStudentId());
        Student student= studentMapper.fromStudentDto(studentDto);
        student.setCourses(loadedStudent.getCourses());
        student.setUser(loadedStudent.getUser());
        Student updatedStudent= studentDao.save(student);
        return studentMapper.fromStudent(student);
    }

    @Override
    public void removeStudent(Long studentId) {
        Student student = loadStudentById(studentId);
        Iterator<Course> courseIterator = student.getCourses().iterator();
        if (courseIterator.hasNext()) {
            Course course = courseIterator.next();
            course.removeStudentFromCourse(student);
        }
        studentDao.deleteById(studentId);
    }
}
