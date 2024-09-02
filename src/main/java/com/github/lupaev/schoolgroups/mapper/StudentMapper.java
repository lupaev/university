package com.github.lupaev.schoolgroups.mapper;

import com.github.lupaev.schoolgroups.dto.StudentDto;
import com.github.lupaev.schoolgroups.entity.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    @Mapping(target = "groupId", source = "group.id")
    @Mapping(target = "dateReceipt", source = "dateReceipt", dateFormat = "dd.MM.yyyy")
    StudentDto toDto(Student student);

    @Mapping(target = "group.id", source = "groupId")
    @Mapping(target = "dateReceipt", source = "dateReceipt", dateFormat = "yyyy-MM-dd")
    Student toEntity(StudentDto studentDto);
}
