package com.javacorner.admin.service.impl;

import com.javacorner.admin.dao.StudentDao;
import com.javacorner.admin.dto.StudentDTO;
import com.javacorner.admin.dto.UserDTO;
import com.javacorner.admin.entity.Course;
import com.javacorner.admin.entity.Student;
import com.javacorner.admin.entity.User;
import com.javacorner.admin.mapper.StudentMapper;
import com.javacorner.admin.service.CourseService;
import com.javacorner.admin.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StudentServiceImplTest {

    @Mock
    private StudentDao studentDao;

    @Mock
    private StudentMapper studentMapper;

    @Mock
    private UserService userService;

    @Mock
    private CourseService courseService;

    @InjectMocks
    private StudentServiceImpl studentService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoadStudentById() {
        Long studentId = 1L;
        Student student = new Student();
        student.setStudentId(studentId);

        when(studentDao.findById(studentId)).thenReturn(Optional.of(student));

        Student result = studentService.loadStudentById(studentId);

        assertNotNull(result);
        assertEquals(1L, result.getStudentId());
    }

    @Test
    void testFindStudentByName() {
        String keyword = "student1";
        int page = 0;
        int size = 10;
        Student student = new Student();
        StudentDTO studentDTO = new StudentDTO();
        Page<Student> pageStudents = new PageImpl<>(Arrays.asList(student));
        PageRequest pageRequest = PageRequest.of(page, size);

        when(studentDao.findStudentsByName(keyword, pageRequest)).thenReturn(pageStudents);
        when(studentMapper.fromStudent(student)).thenReturn(studentDTO);

        Page<StudentDTO> result = studentService.findStudentsByName(keyword, page, size);

        assertEquals(1, result.getContent().size());
        assertEquals(studentDTO, result.getContent().get(0));

        verify(studentDao).findStudentsByName(keyword, pageRequest);
        verify(studentMapper).fromStudent(student);

    }

    @Test
    void testFindStudentByEmail() {
        String email = "student1@gmail.com";
        Student student = new Student();
        StudentDTO studentDTO = new StudentDTO();

        when(studentDao.findStudentByEmail(email)).thenReturn(student);
        when(studentMapper.fromStudent(student)).thenReturn(studentDTO);

        StudentDTO result = studentService.findStudentByEmail(email);

        assertEquals(studentDTO, result);
        verify(studentDao).findStudentByEmail(email);
        verify(studentMapper).fromStudent(student);
    }

    @Test
    void testCreateStudent() {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("student@gmail.com");
        userDTO.setPassword("1234");
        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setUser(userDTO);

        User user = new User();
        user.setEmail("student3@gmail.com");
        Student student = new Student();
        student.setUser(user);
        Student savedStudent = new Student();
        savedStudent.setUser(user);

        when(userService.createUser(studentDTO.getUser().getEmail(), studentDTO.getUser().getPassword())).thenReturn(user);
        doNothing().when(userService).assignRoleToUser(user.getEmail(), "Student");
        when(studentMapper.fromStudentDto(studentDTO)).thenReturn(student);
        when(studentDao.save(student)).thenReturn(savedStudent);
        when(studentMapper.fromStudent(savedStudent)).thenReturn(studentDTO);

        StudentDTO result = studentService.createStudent(studentDTO);

        assertEquals(studentDTO, result);
        verify(userService).createUser(studentDTO.getUser().getEmail(), studentDTO.getUser().getPassword());
        verify(userService).assignRoleToUser(user.getEmail(), "Student");
        verify(studentDao).save(student);
        verify(studentMapper).fromStudent(savedStudent);
    }

    @Test
    void testUpdateStudent() {
        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setStudentId(1L);

        Student loadedStudent = new Student();
        User user = new User();
        Set<Course> courses = new HashSet<>();
        loadedStudent.setUser(user);
        loadedStudent.setCourses(courses);

        Student student = new Student();
        student.setUser(user);
        student.setCourses(courses);

        Student updatedStudent = new Student();
        updatedStudent.setUser(user);
        updatedStudent.setCourses(courses);

        StudentDTO updatedStudentDTO = new StudentDTO();

        when(studentDao.findById(studentDTO.getStudentId())).thenReturn(Optional.of(loadedStudent));
        when(studentMapper.fromStudentDto(studentDTO)).thenReturn(student);
        when(studentDao.save(student)).thenReturn(updatedStudent);
        when(studentMapper.fromStudent(updatedStudent)).thenReturn(updatedStudentDTO);

        StudentDTO result = studentService.updateStudent(studentDTO);

        assertEquals(updatedStudentDTO, result);
        verify(studentDao).findById(studentDTO.getStudentId());
        verify(studentMapper).fromStudentDto(studentDTO);
        verify(studentDao).save(student);
        verify(studentMapper).fromStudent(updatedStudent);
    }


    @Test
    void testRemoveStudent() {
        Long studentId = 1L;
        Student student = new Student();
        Set<Course> courses = new HashSet<>();
        Course course1 = new Course();
        course1.setCourseId(100L);
        Course course2 = new Course();
        course2.setCourseId(200L);
        courses.add(course1);
        courses.add(course2);
        student.setCourses(courses);

        when(studentDao.findById(studentId)).thenReturn(Optional.of(student));

        studentService.removeStudent(studentId);

        verify(courseService).removeCourse(100L);
        verify(courseService).removeCourse(200L);
        verify(studentDao).deleteById(studentId);
    }

}


