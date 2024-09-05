package com.github.lupaev.university.mapper;

import com.github.lupaev.university.dto.GroupDto;
import com.github.lupaev.university.entity.Group;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GroupMapper {
    GroupDto toDto(Group group);

    Group toEntity(GroupDto groupDto);
}
