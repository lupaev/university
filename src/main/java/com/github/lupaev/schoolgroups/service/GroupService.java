package com.github.lupaev.schoolgroups.service;

import com.github.lupaev.schoolgroups.dto.GroupDto;
import com.github.lupaev.schoolgroups.entity.Group;
import com.github.lupaev.schoolgroups.entity.Student;
import com.github.lupaev.schoolgroups.mapper.GroupMapper;
import com.github.lupaev.schoolgroups.repository.GroupRepository;
import com.github.lupaev.schoolgroups.repository.StudentRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final StudentRepository studentRepository;
    private final GroupMapper groupMapper;
    private final EntityManager entityManager;

    public List<GroupDto> getGroups(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<GroupDto> query = cb.createQuery(GroupDto.class);
        Root<Group> groupRoot = query.from(Group.class);
        Join<Group, Student> studentJoin = groupRoot.join("students", JoinType.LEFT);

        query.select(cb.construct(
                        GroupDto.class,
                        groupRoot.get("id"),
                        groupRoot.get("groupNumber"),
                        cb.count(studentJoin.get("id")),
                        groupRoot.get("createdAt")
                ))
                .groupBy(groupRoot.get("id"), groupRoot.get("groupNumber"), groupRoot.get("createdAt"))
                .orderBy(cb.asc(groupRoot.get("createdAt")));

        return entityManager.createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
    }

    @Transactional
    public GroupDto saveGroup(GroupDto groupDto) {
        Group entity = groupMapper.toEntity(groupDto);
        Group save = groupRepository.save(entity);
        return groupMapper.toDto(save);
    }
}
