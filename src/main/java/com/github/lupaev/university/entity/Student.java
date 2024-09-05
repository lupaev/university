package com.github.lupaev.university.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


/**
 * Сущность, представляющая студента в базе данных.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "STUDENT")
public class Student {

    /**
     * Уникальный идентификатор студента.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Полное имя студента.
     */
    @Column(name = "FULL_NAME")
    private String fullName;

    /**
     * Дата зачисления студента.
     */
    @Column(name = "ADMISSION_DATE")
    private LocalDate admissionDate;

    /**
     * Группа, к которой принадлежит студент.
     * Это отношение "многие к одному" с сущностью Group.
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "STUDENT_GROUP_ID")
    private Group group;
}

