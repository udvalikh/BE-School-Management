package com.javacorner.admin.service.impl;

import com.javacorner.admin.dao.CourseDao;
import com.javacorner.admin.dao.InstructorDao;
import com.javacorner.admin.dao.StudentDao;
import com.javacorner.admin.dto.CourseDTO;
import com.javacorner.admin.entity.Course;
import com.javacorner.admin.entity.Instructor;
import com.javacorner.admin.entity.Student;
import com.javacorner.admin.mapper.CourseMapper;
import com.javacorner.admin.service.CourseService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {

    private CourseDao courseDao;
    private CourseMapper courseMapper;
    private InstructorDao instructorDao;
    private StudentDao studentDao;

    public CourseServiceImpl(CourseDao courseDao, CourseMapper courseMapper, InstructorDao instructorDao, StudentDao studentDao) {
        this.courseDao = courseDao;
        this.courseMapper = courseMapper;
        this.instructorDao = instructorDao;
        this.studentDao = studentDao;
    }

    @Override
    public Course loadCourseById(Long courseId) {
        return courseDao.findById(courseId).orElseThrow(()-> new EntityNotFoundException("Course Id with: "+ courseId+ "Not Found"));
    }

    @Override
    public CourseDTO createCourse(CourseDTO courseDto) {
        Course course = courseMapper.fromCourseDto(courseDto);
        Instructor instructor = instructorDao.findById(courseDto.getInstructor().getInstructorId()).orElseThrow(() -> new EntityNotFoundException("Instructor with ID " + courseDto.getInstructor().getInstructorId() + " Not Found"));
        course.setInstructor(instructor);
        Course savedCourse = courseDao.save(course);
        return courseMapper.fromCourse(savedCourse);
    }

    @Override
    public CourseDTO updateCourse(CourseDTO courseDto) {
        Course loadedCourse= loadCourseById(courseDto.getCourseId());
        Instructor instructor= instructorDao.findById(courseDto.getInstructor().getInstructorId()).orElseThrow(()-> new EntityNotFoundException("Instructor id with: " + courseDto.getInstructor().getInstructorId()+ "Not Found"));
        Course course=courseMapper.fromCourseDto(courseDto);
        course.setInstructor(instructor);
        course.setStudents(loadedCourse.getStudents());
        Course updatedCourse= courseDao.save(course);
        return courseMapper.fromCourse(updatedCourse);
    }

    @Override
    public Page<CourseDTO> findCoursesByCourseName(String keyword, int page, int size) {
        PageRequest pageRequest=PageRequest.of(page, size);
        Page<Course> coursesPage=courseDao.findCoursesByCourseNameContains(keyword, pageRequest);
        return new PageImpl<>(coursesPage.getContent().stream().map(course -> courseMapper.fromCourse(course)).collect(Collectors.toList()), pageRequest, coursesPage.getTotalElements());
    }

    @Override
    public void assignStudentToCourse(Long courseId, Long studentId) {
        Student student=studentDao.findById(studentId).orElseThrow(()-> new EntityNotFoundException("Student Id with: "+ studentId+ "Not Found"));
        Course course=loadCourseById(courseId);
        course.assignStudentToCourse(student);
    }

    @Override
    public Page<CourseDTO> fetchCoursesForStudent(Long studentId, int page, int size) {
        PageRequest pageRequest=PageRequest.of(page,size);
        Page<Course> studentCoursePage=courseDao.getCoursesByStudentId(studentId, pageRequest);
        return new PageImpl<>(studentCoursePage.getContent().stream().map(course -> courseMapper.fromCourse(course)).collect(Collectors.toList()), pageRequest, studentCoursePage.getTotalElements());
    }

    @Override
    public Page<CourseDTO> fetchNonEnrolledInCoursesForStudent(Long studentId, int page, int size) {
        PageRequest pageRequest= PageRequest.of(page, size);
        Page<Course> studentNonEnrolledInCoursesPage= courseDao.getNonEnrolledInCoursesByStudentId(studentId, pageRequest);
        return new PageImpl<>(studentNonEnrolledInCoursesPage.getContent().stream().map(course -> courseMapper.fromCourse(course)).collect(Collectors.toList()), pageRequest, studentNonEnrolledInCoursesPage.getTotalElements());
    }

    @Override
    public void removeCourse(Long courseId) {
        courseDao.deleteById(courseId);
    }

    @Override
    public Page<CourseDTO> fetchCoursesForInstructor(Long instructorId, int page, int size) {
        PageRequest pageRequest=PageRequest.of(page, size);
        Page<Course> instructorCoursesPage= courseDao.getCoursesByInstructorId(instructorId, pageRequest);
        return new PageImpl<>(instructorCoursesPage.getContent().stream().map(course -> courseMapper.fromCourse(course)).collect(Collectors.toList()), pageRequest, instructorCoursesPage.getTotalElements());
    }
}
