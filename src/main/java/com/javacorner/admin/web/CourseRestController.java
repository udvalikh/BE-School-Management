package com.javacorner.admin.web;

import com.javacorner.admin.dto.CourseDTO;
import com.javacorner.admin.service.CourseService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/courses")
@CrossOrigin("*")
public class CourseRestController {

    private CourseService courseService;

    public CourseRestController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public Page<CourseDTO> searchCourses(@RequestParam(name="keyword", defaultValue = "") String keyword,
                                         @RequestParam(name="page", defaultValue = "0") int page,
                                         @RequestParam(name="size", defaultValue = "5") int size){
        return courseService.findCoursesByCourseName(keyword, page, size);
        // /courses?keyword=java&page=2&size=3
    }

    @DeleteMapping("/{courseId}")
    public void deleteCourse(@PathVariable Long courseId){
        courseService.removeCourse(courseId);
        // /courses/id
    }

    @PostMapping
    public CourseDTO saveCourse(@RequestBody CourseDTO courseDto){
        return courseService.createCourse(courseDto);
    }

    @PutMapping("/{courseId}")
    public CourseDTO updateCourse(@RequestBody CourseDTO courseDto, @PathVariable Long courseId){
        courseDto.setCourseId(courseId);
        return courseService.updateCourse(courseDto);
    }

    @PostMapping("/{courseId}/enroll/students/{studentId}")
    public void enrollStudentInCourse(@PathVariable Long courseId, @PathVariable Long studentId){
        courseService.assignStudentToCourse(courseId, studentId);
    }
}
