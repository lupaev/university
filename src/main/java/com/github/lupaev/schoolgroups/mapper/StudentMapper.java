package com.github.lupaev.schoolgroups.mapper;

import com.github.lupaev.schoolgroups.dto.StudentDto;
import com.github.lupaev.schoolgroups.entity.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "fullName", source = "fullName")
    @Mapping(target = "dateReceipt", source = "dateReceipt")
    @Mapping(target = "groupId", source = "group.id")
    StudentDto toDto(Student student);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "fullName", source = "fullName")
    @Mapping(target = "dateReceipt", source = "dateReceipt")
    @Mapping(target = "group.id", source = "groupId")
    Student toEntity(StudentDto studentDto);

    List<StudentDto> toDto(List<Student> students);
}
