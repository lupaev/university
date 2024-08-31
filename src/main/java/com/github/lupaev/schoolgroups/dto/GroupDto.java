package com.github.lupaev.schoolgroups.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class GroupDto {
    private Long id;
    private String groupNumber;
    private Integer studentCount;
    private LocalDateTime createdAt;
}
