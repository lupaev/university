package com.github.lupaev.university.service;

import com.github.lupaev.university.dto.GroupDto;
import com.github.lupaev.university.entity.Group;
import com.github.lupaev.university.entity.Student;
import com.github.lupaev.university.mapper.GroupMapper;
import com.github.lupaev.university.repository.GroupRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Сервис для управления группами.
 */
@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final GroupMapper groupMapper;
    private final EntityManager entityManager;

    /**
     * Получает список групп с учетом пагинации.
     *
     * @param page номер страницы, начиная с 0.
     * @param size количество элементов на странице.
     * @return список DTO групп с количеством студентов в каждой группе.
     */
    public List<GroupDto> getGroups(int page, int size) {
        // Создаем объект Pageable для управления пагинацией
        Pageable pageable = PageRequest.of(page, size);

        // Получаем CriteriaBuilder из EntityManager для построения запросов
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Создаем CriteriaQuery для типа GroupDto, который будет возвращен
        CriteriaQuery<GroupDto> query = cb.createQuery(GroupDto.class);

        // Определяем корневую сущность для запроса - это будет сущность Group
        Root<Group> groupRoot = query.from(Group.class);

        // Создаем соединение с сущностью Student, чтобы получить количество студентов в каждой группе
        Join<Group, Student> studentJoin = groupRoot.join("students", JoinType.LEFT);

        // Определяем, какие поля будут выбраны в результате запроса
        // Используем, cb.construct для создания нового экземпляра GroupDto с полями, которые мы выбираем
        query.select(cb.construct(
                        GroupDto.class,
                        groupRoot.get("id"), // Идентификатор группы
                        groupRoot.get("groupNumber"), // Номер группы
                        cb.count(studentJoin.get("id")), // Количество студентов в группе
                        groupRoot.get("createdAt") // Дата создания группы
                ))
                // Группируем результаты по идентификатору, номеру и дате создания группы
                .groupBy(groupRoot.get("id"), groupRoot.get("groupNumber"), groupRoot.get("createdAt"))
                // Устанавливаем порядок сортировки по дате создания группы
                .orderBy(cb.asc(groupRoot.get("createdAt")));

        // Выполняем запрос с учетом пагинации
        // setFirstResult и setMaxResults используются для ограничения количества возвращаемых результатов
        return entityManager.createQuery(query)
                .setFirstResult((int) pageable.getOffset()) // Устанавливаем начальный индекс для пагинации
                .setMaxResults(pageable.getPageSize()) // Устанавливаем максимальное количество результатов на страницу
                .getResultList(); // Получаем список результатов
    }

    /**
     * Сохраняет информацию о группе.
     *
     * @param groupDto DTO группы для сохранения.
     * @return сохраненный DTO группы.
     */
    @Transactional
    public GroupDto saveGroup(GroupDto groupDto) {
        try {
            // Преобразование DTO в сущность
            Group entity = groupMapper.toEntity(groupDto);

            // Сохранение сущности в базе данных
            Group savedEntity = groupRepository.save(entity);

            // Преобразование сохраненной сущности обратно в DTO
            return groupMapper.toDto(savedEntity);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Группа с таким номером уже существует.", e);
        }
    }
}
