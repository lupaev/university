package com.github.lupaev.university.service;

import com.github.lupaev.university.dto.StudentDto;
import com.github.lupaev.university.entity.Student;
import com.github.lupaev.university.mapper.StudentMapper;
import com.github.lupaev.university.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Сервис для управления студентами.
 */
@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    /**
     * Получает список студентов, принадлежащих определенной группе, с учетом пагинации.
     *
     * @param groupId идентификатор группы, студенты которой должны быть получены.
     * @param page номер страницы, начиная с 0.
     * @param size количество элементов на странице.
     * @return список DTO студентов, отсортированных по полному имени.
     */
    public List<StudentDto> getStudentsByGroup(Long groupId, int page, int size) {
        // Создаем объект Pageable для управления пагинацией
        Pageable pageable = PageRequest.of(page, size);

        // Получаем страницу студентов, соответствующих спецификации
        Page<Student> students = studentRepository.findAll(hasGroupIdAndOrderByFullNameAsc(groupId), pageable);

        // Преобразуем сущности студентов в DTO и возвращаем список
        return students.stream().map(studentMapper::toDto).toList();
    }

    /**
     * Создает спецификацию для фильтрации студентов по идентификатору группы и сортировки по полному имени.
     *
     * @param groupId идентификатор группы.
     * @return спецификация для фильтрации и сортировки студентов.
     */
    private Specification<Student> hasGroupIdAndOrderByFullNameAsc(Long groupId) {
        return (root, query, cb) -> {
            // Устанавливаем порядок сортировки по полному имени студента
            query.orderBy(cb.asc(root.get("fullName")));

            // Возвращаем условие фильтрации по идентификатору группы
            return cb.equal(root.get("group").get("id"), groupId);
        };
    }

    /**
     * Сохраняет информацию о студенте.
     *
     * @param studentDto DTO студента для сохранения.
     * @return сохраненный DTO студента.
     */
    @Transactional
    public StudentDto saveStudent(StudentDto studentDto) {
        // Преобразуем DTO в сущность и сохраняем в базе данных
        Student saved = studentRepository.save(studentMapper.toEntity(studentDto));

        // Преобразуем сохраненную сущность обратно в DTO и возвращаем
        return studentMapper.toDto(saved);
    }

    /**
     * Удаляет студента по его идентификатору.
     *
     * @param id идентификатор студента для удаления.
     */
    @Transactional
    public void deleteStudent(Long id) {
        // Удаляем студента из базы данных по идентификатору
        studentRepository.deleteById(id);
    }
}
