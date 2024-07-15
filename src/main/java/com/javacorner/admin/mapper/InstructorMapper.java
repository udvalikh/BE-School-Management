package com.javacorner.admin.mapper;

import com.javacorner.admin.dto.InstructorDTO;
import com.javacorner.admin.entity.Instructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;


@Service
public class InstructorMapper {

    public InstructorDTO fromInstructor(Instructor instructor) {
        InstructorDTO instructorDto = new InstructorDTO();
        BeanUtils.copyProperties(instructor, instructorDto);
        return instructorDto;
    }

    public Instructor fromInstructorDto(InstructorDTO instructorDto) {
        Instructor instructor = new Instructor();
        BeanUtils.copyProperties(instructorDto, instructor);
        return instructor;
    }

}
