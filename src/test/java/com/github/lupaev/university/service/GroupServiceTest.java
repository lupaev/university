package com.github.lupaev.university.service;

import com.github.lupaev.university.dto.GroupDto;
import com.github.lupaev.university.entity.Group;
import com.github.lupaev.university.entity.Student;
import com.github.lupaev.university.mapper.GroupMapper;
import com.github.lupaev.university.repository.GroupRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private GroupMapper groupMapper;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private GroupService groupService;

    @Test
    public void testGetGroups() {
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);

        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        CriteriaQuery<GroupDto> query = mock(CriteriaQuery.class);
        Root<Group> groupRoot = mock(Root.class);
        Join<Group, Student> studentJoin = mock(Join.class);
        TypedQuery<GroupDto> typedQuery = mock(TypedQuery.class);
        Expression<Long> countExpression = mock(Expression.class);
        Order order = mock(Order.class);

        when(entityManager.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(GroupDto.class)).thenReturn(query);
        when(query.from(Group.class)).thenReturn(groupRoot);
        when(groupRoot.join("students", JoinType.LEFT)).thenReturn((Join) studentJoin);
        when(cb.count(studentJoin.get("id"))).thenReturn(countExpression);
        when(cb.asc(any())).thenReturn(order);

        when(query.select(any())).thenReturn(query);
        when(query.groupBy(any(), any(), any())).thenReturn(query);
        when(query.orderBy(any(Order.class))).thenReturn(query);

        when(entityManager.createQuery(query)).thenReturn(typedQuery);
        when(typedQuery.setFirstResult((int) pageable.getOffset())).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(pageable.getPageSize())).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Collections.emptyList());

        List<GroupDto> result = groupService.getGroups(page, size);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(entityManager, times(1)).getCriteriaBuilder();
        verify(entityManager, times(1)).createQuery(query);
        verify(typedQuery, times(1)).setFirstResult((int) pageable.getOffset());
        verify(typedQuery, times(1)).setMaxResults(pageable.getPageSize());
        verify(typedQuery, times(1)).getResultList();
    }

    @Test
    public void testSaveGroup() {
        GroupDto groupDto = new GroupDto();
        groupDto.setGroupNumber("101");

        Group group = new Group();
        group.setGroupNumber("101");

        when(groupMapper.toEntity(groupDto)).thenReturn(group);
        when(groupRepository.save(group)).thenReturn(group);
        when(groupMapper.toDto(group)).thenReturn(groupDto);

        GroupDto result = groupService.saveGroup(groupDto);

        assertNotNull(result);
        assertEquals("101", result.getGroupNumber());

        verify(groupRepository, times(1)).save(group);
        verify(groupMapper, times(1)).toEntity(groupDto);
        verify(groupMapper, times(1)).toDto(group);
    }
}