package com.github.lupaev.university.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Сущность, представляющая группу в базе данных.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "STUDENT_GROUP")
public class Group {

    /**
     * Уникальный идентификатор группы.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Номер группы.
     */
    @Column(name = "GROUP_NUMBER")
    private String groupNumber;

    /**
     * Дата и время создания группы.
     */
    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    /**
     * Список студентов, принадлежащих группе.
     * Это отношение "один ко многим", связанное полем 'group' в сущности Student.
     */
    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
    private List<Student> students;
}
