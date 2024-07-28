package com.javacorner.admin.web;

import com.javacorner.admin.dto.CourseDTO;
import com.javacorner.admin.dto.InstructorDTO;
import com.javacorner.admin.entity.User;
import com.javacorner.admin.service.CourseService;
import com.javacorner.admin.service.InstructorService;
import com.javacorner.admin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/instructors")
@CrossOrigin("*")
public class InstructorRestController {

    @Autowired
    private InstructorService instructorService;

    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;

    @GetMapping
    @PreAuthorize("hasAuthority('Admin')")
    public Page<InstructorDTO> searchInstructors(@RequestParam(name="keyword", defaultValue = "") String keyword,
                                                 @RequestParam(name="page", defaultValue = "0") int page,
                                                 @RequestParam(name="size", defaultValue = "5") int size){
        return instructorService.findInstructorByName(keyword, page, size);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('Admin')")
    public List<InstructorDTO> findAllInstructors(){
        return instructorService.fetchInstructors();
    }

    @DeleteMapping("/{instructorId}")
    @PreAuthorize("hasAuthority('Admin')")
    public void deleteInstructor(@PathVariable Long instructorId){
        instructorService.removeInstructor(instructorId);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('Admin')")
    public InstructorDTO saveInstructor(@RequestBody InstructorDTO instructorDto){
        User user= userService.loadUserByEmail(instructorDto.getUser().getEmail());
        if(user != null) throw new RuntimeException("Email already exist");
       return instructorService.createInstructor(instructorDto);
    }

    @PutMapping("/{instructorId}")
    @PreAuthorize("hasAuthority('Instructor')")
    public InstructorDTO updateInstructor(@RequestBody InstructorDTO instructorDto, @PathVariable Long instructorId){
        instructorDto.setInstructorId(instructorId);
        return instructorService.updateInstructor(instructorDto);
    }

    @GetMapping("/{instructorId}/courses")
    @PreAuthorize("hasAnyAuthority('Admin', 'Instructor')")
    public Page<CourseDTO> courseByInstructorId(@PathVariable Long instructorId,
                                                @RequestParam(name="page", defaultValue = "0") int page,
                                                @RequestParam(name="size", defaultValue = "5") int size){
        return courseService.fetchCoursesForInstructor(instructorId, page, size);
    }

    @GetMapping("/find")
    @PreAuthorize("hasAuthority('Instructor')")
    public InstructorDTO loadInstructorByEmail(@RequestParam(name="email", defaultValue ="") String email){
        return instructorService.loadInstructorByEmail(email);
    }
}
