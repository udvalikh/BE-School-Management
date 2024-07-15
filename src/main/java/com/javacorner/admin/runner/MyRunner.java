package com.javacorner.admin.runner;

import com.javacorner.admin.dto.CourseDTO;
import com.javacorner.admin.dto.InstructorDTO;
import com.javacorner.admin.dto.StudentDTO;
import com.javacorner.admin.dto.UserDTO;
import com.javacorner.admin.service.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
@Component
public class MyRunner implements CommandLineRunner {

    private RoleService roleService;
    private UserService userService;
    private InstructorService instructorService;
    private CourseService courseService;
    private StudentService studentService;

    public MyRunner(RoleService roleService, UserService userService, InstructorService instructorService, CourseService courseService, StudentService studentService) {
        this.roleService = roleService;
        this.userService = userService;
        this.instructorService = instructorService;
        this.courseService = courseService;
        this.studentService = studentService;
    }

    @Override
    public void run(String... args) throws Exception {
        createRole();
        createAdmin();
        createInstructor();
       createCourse();
        StudentDTO studentDto= createStudent();
        assignCourseToStudent(studentDto);
        createStudents();

    }

    private void createStudents() {
        for (int i=1; i<10; i++){
            StudentDTO studentDto= new StudentDTO();
            studentDto.setFirstName("StudentFN"+i);
            studentDto.setLastName("StudentLN"+i);
            studentDto.setLevel("intermediate"+i);
            UserDTO userDto=new UserDTO();
            userDto.setEmail("student"+i+"@gmail.com");
            userDto.setPassword("1234");
            studentDto.setUser(userDto);
            studentService.createStudent(studentDto);
        }
    }


    private void createRole() {
        Arrays.asList("Admin", "Instructor", "Student").forEach(role-> roleService.createRole(role));
    }

    private void createAdmin() {
        userService.createUser("admin@gmail.com", "1234");
        userService.assignRoleToUser("admin@gmail.com", "Admin");
    }

    private void createInstructor() {
        for(int i=0; i<10; i++){
            InstructorDTO instructorDto=new InstructorDTO();
            instructorDto.setFirstName("Instructor"+i+"FN");
            instructorDto.setLastName("Instructor"+i+"LN");
            instructorDto.setSummary("master"+i);
            UserDTO userDto=new UserDTO();
            userDto.setEmail("instructor"+i+"@gmail.com");
            userDto.setPassword("1234");
            instructorDto.setUser(userDto);
            instructorService.createInstructor(instructorDto);
        }
    }

    private void createCourse() {
        for(int i=0; i<20; i++){
            CourseDTO courseDto= new CourseDTO();
            courseDto.setCourseName("Java"+i);
            courseDto.setCourseDuration(i+"hours");
            courseDto.setCourseDescription("Java Course"+i);
            InstructorDTO instructorDto= new InstructorDTO();
            instructorDto.setInstructorId(1L);
            courseDto.setInstructor(instructorDto);
            courseService.createCourse(courseDto);
        }
    }

    private StudentDTO createStudent() {
        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setFirstName("studentFN");
        studentDTO.setLastName("studentLN");
        studentDTO.setLevel("intermediate");
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("student2nd@gmail.com");
        userDTO.setPassword("1234");
        studentDTO.setUser(userDTO);
        return studentService.createStudent(studentDTO);
    }

    private void assignCourseToStudent(StudentDTO student) {
        courseService.assignStudentToCourse(1L, student.getStudentId());
    }


}
