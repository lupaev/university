package com.github.lupaev.schoolgroups.service;

import com.github.lupaev.schoolgroups.dto.GroupDto;
import com.github.lupaev.schoolgroups.entity.Group;
import com.github.lupaev.schoolgroups.mapper.GroupMapper;
import com.github.lupaev.schoolgroups.repository.GroupRepository;
import com.github.lupaev.schoolgroups.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.github.lupaev.schoolgroups.specification.GroupSpecification.orderByCreatedAtAsc;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final StudentRepository studentRepository;
    private final GroupMapper groupMapper;

    public List<GroupDto> getGroups(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Group> groupPage = groupRepository.findAll(orderByCreatedAtAsc(), pageable);
        return groupPage.stream()
                .map(group -> {
                    GroupDto dto = groupMapper.toDto(group);
                    dto.setStudentCount(studentRepository.countByGroupId(group.getId()).intValue());
                    return dto;
                })
                .toList();
    }

    public GroupDto saveGroup(GroupDto groupDto) {
        return groupMapper.toDto(groupRepository.save(groupMapper.toEntity(groupDto)));
    }
}
