package com.github.lupaev.university.mapper;

import com.github.lupaev.university.dto.StudentDto;
import com.github.lupaev.university.entity.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    @Mapping(target = "groupId", source = "group.id")
    @Mapping(target = "admissionDate", source = "admissionDate", dateFormat = "dd.MM.yyyy")
    StudentDto toDto(Student student);

    @Mapping(target = "group.id", source = "groupId")
    @Mapping(target = "admissionDate", source = "admissionDate", dateFormat = "yyyy-MM-dd")
    Student toEntity(StudentDto studentDto);
}
