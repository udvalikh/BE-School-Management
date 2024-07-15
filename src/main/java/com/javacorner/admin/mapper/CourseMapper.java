package com.javacorner.admin.mapper;

import com.javacorner.admin.dto.CourseDTO;
import com.javacorner.admin.entity.Course;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CourseMapper {
@Autowired
    private InstructorMapper instructorMapper;

    public CourseDTO fromCourse(Course course){
        CourseDTO courseDto= new CourseDTO();
        BeanUtils.copyProperties(course, courseDto);
        courseDto.setInstructor(instructorMapper.fromInstructor(course.getInstructor()));
        return courseDto;
    }

    public Course fromCourseDto(CourseDTO courseDto){
        Course course= new Course();
        BeanUtils.copyProperties(courseDto, course);
        return course;
    }
}
