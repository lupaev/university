package com.github.lupaev.university.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Объект передачи данных (DTO) для представления студента.
 */
@Getter
@Setter
public class StudentDto {

    /**
     * Уникальный идентификатор студента.
     */
    private Long id;

    /**
     * Дата зачисления студента.
     */
    private String admissionDate;

    /**
     * Полное имя студента.
     */
    private String fullName;

    /**
     * Идентификатор группы, к которой принадлежит студент.
     */
    private Long groupId;
}
