package com.github.lupaev.university.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotNull
    private String admissionDate;

    /**
     * Полное имя студента.
     */
    @NotBlank
    private String fullName;

    /**
     * Идентификатор группы, к которой принадлежит студент.
     */
    private Long groupId;
}
