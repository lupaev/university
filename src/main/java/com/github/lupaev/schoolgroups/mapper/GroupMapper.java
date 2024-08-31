package com.github.lupaev.schoolgroups.mapper;

import com.github.lupaev.schoolgroups.dto.GroupDto;
import com.github.lupaev.schoolgroups.entity.Group;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GroupMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "groupNumber", source = "groupNumber")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "studentCount", source = "group", qualifiedByName = "getStudentCount")
    GroupDto toDto(Group group);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "groupNumber", source = "groupNumber")
    @Mapping(target = "createdAt", source = "createdAt")
    Group toEntity(GroupDto groupDto);

    List<GroupDto> toDto(List<Group> groups);

    @Named(value = "getStudentCount")
    default Integer getStudentCount(Group group) {
        return group.getStudents() != null ? group.getStudents().size() : 0;
    }
}
