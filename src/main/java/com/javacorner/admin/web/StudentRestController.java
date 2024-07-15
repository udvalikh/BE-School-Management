package com.javacorner.admin.web;

import com.javacorner.admin.dto.CourseDTO;
import com.javacorner.admin.dto.StudentDTO;
import com.javacorner.admin.entity.User;
import com.javacorner.admin.service.CourseService;
import com.javacorner.admin.service.StudentService;
import com.javacorner.admin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/students")
@CrossOrigin("*")
public class StudentRestController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;

    @GetMapping
    public Page<StudentDTO> searchStudents(@RequestParam(name = "keyword", defaultValue = "") String keyword,
                                           @RequestParam(name= "page", defaultValue = "0") int page,
                                           @RequestParam(name = "size", defaultValue ="5") int size){
       return studentService.findStudentsByName(keyword, page, size);
    }

    @DeleteMapping("/{studentId}")
    public void deleteStudentById(@PathVariable Long studentId){
        studentService.removeStudent(studentId);
    }

    @PostMapping
    public StudentDTO saveStudent(@RequestBody StudentDTO studentDto){
        User user=userService.loadUserByEmail(studentDto.getUser().getEmail());
        if(user != null) throw new RuntimeException("Email Already Exist");
        return studentService.createStudent(studentDto);
    }

    @PutMapping("/{studentId}")
    public StudentDTO updateStudent(@RequestBody StudentDTO studentDto, @PathVariable Long studentId){
        studentDto.setStudentId(studentId);
        return studentService.updateStudent(studentDto);
    }

    @GetMapping("/{studentId}/courses")
    public Page<CourseDTO> coursesByStudentId(@PathVariable Long studentId,
                                              @RequestParam(name= "page", defaultValue = "0") int page,
                                              @RequestParam(name="size", defaultValue = "5") int size){
       return courseService.fetchCoursesForStudent(studentId, page, size);
    }

    @GetMapping("/{studentId}/other-courses")
    public Page<CourseDTO> nonSubscribedCoursesByStudentId(@PathVariable Long studentId,
                                                           @RequestParam(name= "page", defaultValue = "0") int page,
                                                           @RequestParam(name="size", defaultValue = "5") int size){
        return courseService.fetchNonEnrolledInCoursesForStudent(studentId, page, size);
    }

    @GetMapping("/find")
    public StudentDTO loadStudentByEmail(@RequestParam(name="email", defaultValue = "") String email){
        return studentService.findStudentByEmail(email);
    }

}
