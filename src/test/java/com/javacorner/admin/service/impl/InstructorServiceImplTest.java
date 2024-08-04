package com.javacorner.admin.service.impl;

import com.javacorner.admin.dao.InstructorDao;
import com.javacorner.admin.dto.InstructorDTO;
import com.javacorner.admin.dto.UserDTO;
import com.javacorner.admin.entity.Course;
import com.javacorner.admin.entity.Instructor;
import com.javacorner.admin.entity.User;
import com.javacorner.admin.mapper.InstructorMapper;
import com.javacorner.admin.service.CourseService;
import com.javacorner.admin.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InstructorServiceImplTest {

    @Mock
    private InstructorDao instructorDao;

    @Mock
    private InstructorMapper instructorMapper;

    @Mock
    private UserService userService;

    @Mock
    private CourseService courseService;

    @InjectMocks
    private InstructorServiceImpl instructorService;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoadInstructorById(){
        Long instructorId=1L;
        Instructor instructor = new Instructor();
        instructor.setInstructorId(instructorId);
        when(instructorDao.findById(instructorId)).thenReturn(Optional.of(instructor));

        Instructor result = instructorService.loadInstructorById(instructorId);
        assertNotNull(result);
        Assertions.assertEquals(1L, result.getInstructorId());
    }

    @Test
    void testFindInstructorByName(){
        String keyword="Instructor1";
        int page=0;
        int size=10;
        Instructor instructor =new Instructor();
        InstructorDTO instructorDTO = new InstructorDTO();
        Page<Instructor> pageInstructors = new PageImpl<>(Arrays.asList(instructor));
        PageRequest pageRequest = PageRequest.of(page, size);

        when(instructorDao.findInstructorsByName(keyword, pageRequest)).thenReturn(pageInstructors);
        when(instructorMapper.fromInstructor(instructor)).thenReturn(instructorDTO);

        Page<InstructorDTO> result = instructorService.findInstructorByName(keyword, page, size);

        assertEquals(1, result.getContent().size());
        assertEquals(instructorDTO, result.getContent().get(0));

        verify(instructorDao).findInstructorsByName(keyword, pageRequest);
        verify(instructorMapper).fromInstructor(instructor);
    }

    @Test
    void testLoadInstructorByEmail(){
        String email="instructor1@gmail.com";
        Instructor instructor= new Instructor();
        InstructorDTO instructorDTO = new InstructorDTO();

        when(instructorDao.findInstructorByEmail(email)).thenReturn(instructor);
        when(instructorMapper.fromInstructor(instructor)).thenReturn(instructorDTO);

        instructorService.loadInstructorByEmail(email);

        verify(instructorDao).findInstructorByEmail(email);
        verify(instructorMapper).fromInstructor(instructor);
    }

    @Test
    void testCreateInstructor(){
        InstructorDTO instructorDTO = new InstructorDTO();
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("instructor@gmail.com");
        userDTO.setPassword("1234");
        instructorDTO.setUser(userDTO);

        User user =new User();
        user.setEmail("instructor@gmail.com");

        Instructor instructor= new Instructor();
        instructor.setUser(user);

        Instructor savedInstructor = new Instructor();
        savedInstructor.setUser(user);

        when(userService.createUser(instructorDTO.getUser().getEmail(), instructorDTO.getUser().getPassword())).thenReturn(user);
        doNothing().when(userService).assignRoleToUser(user.getEmail(), "Instructor");
        when(instructorMapper.fromInstructorDto(instructorDTO)).thenReturn(instructor);
        when(instructorDao.save(instructor)).thenReturn(savedInstructor);
        when(instructorMapper.fromInstructor(savedInstructor)).thenReturn(instructorDTO);

        InstructorDTO result = instructorService.createInstructor(instructorDTO);

        assertEquals(instructorDTO, result);
        verify(userService).createUser(instructorDTO.getUser().getEmail(), instructorDTO.getUser().getPassword());
        verify(userService).assignRoleToUser(user.getEmail(), "Instructor");
        verify(instructorDao).save(instructor);
        verify(instructorMapper).fromInstructor(savedInstructor);

    }


    @Test
    public void testUpdateInstructor_Success() {
        // Arrange
        InstructorDTO instructorDto = new InstructorDTO();
        instructorDto.setInstructorId(1L);

        Instructor loadedInstructor = new Instructor();
        User user = new User();
        loadedInstructor.setUser(user);
       Set<Course> courses= new HashSet<>();
        loadedInstructor.setCourses(courses);

        Instructor instructor = new Instructor();
        instructor.setUser(user);
        instructor.setCourses(courses);

        Instructor updatedInstructor = new Instructor();
        updatedInstructor.setUser(user);
        updatedInstructor.setCourses(courses);

        InstructorDTO updatedInstructorDto = new InstructorDTO();

        when(instructorDao.findById(instructorDto.getInstructorId())).thenReturn(Optional.of(loadedInstructor));
        when(instructorMapper.fromInstructorDto(instructorDto)).thenReturn(instructor);
        when(instructorDao.save(instructor)).thenReturn(updatedInstructor);
        when(instructorMapper.fromInstructor(updatedInstructor)).thenReturn(updatedInstructorDto);

        // Act
        InstructorDTO result = instructorService.updateInstructor(instructorDto);

        // Assert
        assertEquals(updatedInstructorDto, result);
        verify(instructorDao).findById(instructorDto.getInstructorId());
        verify(instructorMapper).fromInstructorDto(instructorDto);
        verify(instructorDao).save(instructor);
        verify(instructorMapper).fromInstructor(updatedInstructor);
    }

    @Test
    void testFetchInstructors(){
        instructorDao.findAll();
        verify(instructorDao).findAll();
    }

    @Test
    void testRemoveInstructor(){
        Long instructorId=1L;
        Instructor instructor = new Instructor();
        Set<Course> courses= new HashSet<>();
        Course course1 = new Course();
        course1.setCourseId(10L);
        Course course2= new Course();
        course2.setCourseId(20l);
        courses.add(course1);
        courses.add(course2);
        instructor.setCourses(courses);

        when(instructorDao.findById(instructorId)).thenReturn(Optional.of(instructor));

        instructorService.removeInstructor(instructorId);

        verify(courseService).removeCourse(10L);
        verify(courseService).removeCourse(20L);
        verify(instructorDao).deleteById(instructorId);
    }
}


