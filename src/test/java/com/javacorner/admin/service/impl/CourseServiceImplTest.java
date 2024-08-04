package com.javacorner.admin.service.impl;

import com.javacorner.admin.dao.CourseDao;
import com.javacorner.admin.dao.InstructorDao;
import com.javacorner.admin.dao.StudentDao;
import com.javacorner.admin.dto.CourseDTO;
import com.javacorner.admin.dto.InstructorDTO;
import com.javacorner.admin.entity.Course;
import com.javacorner.admin.entity.Instructor;
import com.javacorner.admin.entity.Student;
import com.javacorner.admin.mapper.CourseMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceImplTest {

    @Mock
    private CourseDao courseDao;

    @Mock
    private CourseMapper courseMapper;

    @Mock
    private InstructorDao instructorDao;

    @Mock
    private StudentDao studentDao;

    @InjectMocks
    private CourseServiceImpl courseService;
    
    @Test
    void testLoadCourseById() {
        Long courseId = 1L;
        Course course = new Course();
        when(courseDao.findById(courseId)).thenReturn(Optional.of(course));

        Course result = courseService.loadCourseById(courseId);

        assertEquals(course, result);
        verify(courseDao).findById(courseId);
    }

    @Test
    void testLoadCourseByIdNotFound() {
        Long courseId = 1L;
        when(courseDao.findById(courseId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            courseService.loadCourseById(courseId);
        });

        assertEquals("Course Id with: 1Not Found", exception.getMessage());
    }

    @Test
    void testCreateCourse() {
        CourseDTO courseDto = new CourseDTO();
        InstructorDTO instructorDto = new InstructorDTO();
        instructorDto.setInstructorId(1L);
        courseDto.setInstructor(instructorDto);
        Course course = new Course();
        Course savedCourse = new Course();
        CourseDTO savedCourseDto = new CourseDTO();

        when(courseMapper.fromCourseDto(courseDto)).thenReturn(course);
        when(instructorDao.findById(courseDto.getInstructor().getInstructorId())).thenReturn(Optional.of(new Instructor()));
        when(courseDao.save(course)).thenReturn(savedCourse);
        when(courseMapper.fromCourse(savedCourse)).thenReturn(savedCourseDto);

        CourseDTO result = courseService.createCourse(courseDto);

        assertEquals(savedCourseDto, result);
        verify(courseMapper).fromCourseDto(courseDto);
        verify(instructorDao).findById(courseDto.getInstructor().getInstructorId());
        verify(courseDao).save(course);
        verify(courseMapper).fromCourse(savedCourse);
    }

    @Test
    void testUpdateCourse() {
        CourseDTO courseDto = new CourseDTO();
        courseDto.setCourseId(1L);
        InstructorDTO instructorDto = new InstructorDTO();
        instructorDto.setInstructorId(1L);
        courseDto.setInstructor(instructorDto);
        Course loadedCourse = new Course();
        Course course = new Course();
        Course updatedCourse = new Course();
        CourseDTO updatedCourseDto = new CourseDTO();

        when(courseDao.findById(courseDto.getCourseId())).thenReturn(Optional.of(loadedCourse));
        when(instructorDao.findById(courseDto.getInstructor().getInstructorId())).thenReturn(Optional.of(new Instructor()));
        when(courseMapper.fromCourseDto(courseDto)).thenReturn(course);
        when(courseDao.save(course)).thenReturn(updatedCourse);
        when(courseMapper.fromCourse(updatedCourse)).thenReturn(updatedCourseDto);

        CourseDTO result = courseService.updateCourse(courseDto);

        assertEquals(updatedCourseDto, result);
        verify(courseDao).findById(courseDto.getCourseId());
        verify(instructorDao).findById(courseDto.getInstructor().getInstructorId());
        verify(courseMapper).fromCourseDto(courseDto);
        verify(courseDao).save(course);
        verify(courseMapper).fromCourse(updatedCourse);
    }

    @Test
    void testFindCoursesByCourseName() {
        String keyword = "test";
        int page = 0;
        int size = 10;
        Course course = new Course();
        CourseDTO courseDto = new CourseDTO();
        Page<Course> coursesPage = new PageImpl<>(Arrays.asList(course));
        PageRequest pageRequest = PageRequest.of(page, size);

        when(courseDao.findCoursesByCourseNameContains(keyword, pageRequest)).thenReturn(coursesPage);
        when(courseMapper.fromCourse(course)).thenReturn(courseDto);

        Page<CourseDTO> result = courseService.findCoursesByCourseName(keyword, page, size);

        assertEquals(1, result.getContent().size());
        assertEquals(courseDto, result.getContent().get(0));
        verify(courseDao).findCoursesByCourseNameContains(keyword, pageRequest);
        verify(courseMapper).fromCourse(course);
    }

    @Test
    void testAssignStudentToCourse() {
        Long courseId = 1L;
        Long studentId = 1L;
        Student student = new Student();
        Course course = new Course();
        when(studentDao.findById(studentId)).thenReturn(Optional.of(student));
        when(courseDao.findById(courseId)).thenReturn(Optional.of(course));

        courseService.assignStudentToCourse(courseId, studentId);

        verify(studentDao).findById(studentId);
        verify(courseDao).findById(courseId);
        // The save method is necessary if assignStudentToCourse method has a save call.
        // verify(courseDao).save(course);
    }

    @Test
    void testFetchCoursesForStudent() {
        Long studentId = 1L;
        int page = 0;
        int size = 10;
        Course course = new Course();
        CourseDTO courseDto = new CourseDTO();
        Page<Course> coursesPage = new PageImpl<>(Arrays.asList(course));
        PageRequest pageRequest = PageRequest.of(page, size);

        when(courseDao.getCoursesByStudentId(studentId, pageRequest)).thenReturn(coursesPage);
        when(courseMapper.fromCourse(course)).thenReturn(courseDto);

        Page<CourseDTO> result = courseService.fetchCoursesForStudent(studentId, page, size);

        assertEquals(1, result.getContent().size());
        assertEquals(courseDto, result.getContent().get(0));
        verify(courseDao).getCoursesByStudentId(studentId, pageRequest);
        verify(courseMapper).fromCourse(course);
    }

    @Test
    void testFetchNonEnrolledInCoursesForStudent() {
        Long studentId = 1L;
        int page = 0;
        int size = 10;
        Course course = new Course();
        CourseDTO courseDto = new CourseDTO();
        Page<Course> coursesPage = new PageImpl<>(Arrays.asList(course));
        PageRequest pageRequest = PageRequest.of(page, size);

        when(courseDao.getNonEnrolledInCoursesByStudentId(studentId, pageRequest)).thenReturn(coursesPage);
        when(courseMapper.fromCourse(course)).thenReturn(courseDto);

        Page<CourseDTO> result = courseService.fetchNonEnrolledInCoursesForStudent(studentId, page, size);

        assertEquals(1, result.getContent().size());
        assertEquals(courseDto, result.getContent().get(0));
        verify(courseDao).getNonEnrolledInCoursesByStudentId(studentId, pageRequest);
        verify(courseMapper).fromCourse(course);
    }

    @Test
    void testRemoveCourse() {
        Long courseId = 1L;

        courseService.removeCourse(courseId);

        verify(courseDao).deleteById(courseId);
    }

    @Test
    void testFetchCoursesForInstructor() {
        Long instructorId = 1L;
        int page = 0;
        int size = 10;
        Course course = new Course();
        CourseDTO courseDto = new CourseDTO();
        Page<Course> coursesPage = new PageImpl<>(Arrays.asList(course));
        PageRequest pageRequest = PageRequest.of(page, size);

        when(courseDao.getCoursesByInstructorId(instructorId, pageRequest)).thenReturn(coursesPage);
        when(courseMapper.fromCourse(course)).thenReturn(courseDto);

        Page<CourseDTO> result = courseService.fetchCoursesForInstructor(instructorId, page, size);

        assertEquals(1, result.getContent().size());
        assertEquals(courseDto, result.getContent().get(0));
        verify(courseDao).getCoursesByInstructorId(instructorId, pageRequest);
        verify(courseMapper).fromCourse(course);
    }
}
