package com.github.lupaev.university.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Объект передачи данных (DTO) для представления группы.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GroupDto {

    /**
     * Уникальный идентификатор группы.
     */
    private Long id;

    /**
     * Номер группы.
     */
    private String groupNumber;

    /**
     * Количество студентов в группе.
     */
    private Long studentCount;

    /**
     * Дата и время создания группы.
     */
    private LocalDateTime createdAt;
}
