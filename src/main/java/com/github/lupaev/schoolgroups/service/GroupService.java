package com.github.lupaev.schoolgroups.service;

import com.github.lupaev.schoolgroups.dto.GroupDto;
import com.github.lupaev.schoolgroups.entity.Group;
import com.github.lupaev.schoolgroups.mapper.GroupMapper;
import com.github.lupaev.schoolgroups.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final GroupMapper groupMapper;

    public List<GroupDto> getAllGroups() {
        List<Group> groups = groupRepository.findAllByOrderByCreatedAtAsc();
        return groups.stream()
                .map(group -> {
                    GroupDto dto = groupMapper.toDto(group);
                    dto.setStudentCount(groupRepository.countStudentsByGroupId(group.getId()).intValue());
                    return dto;
                })
                .toList();
    }

    public GroupDto saveGroup(GroupDto groupDto) {
        return groupMapper.toDto(groupRepository.save(groupMapper.toEntity(groupDto)));
    }
}
