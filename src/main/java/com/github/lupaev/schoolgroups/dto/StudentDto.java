package com.github.lupaev.schoolgroups.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class StudentDto {
    private Long id;
    private LocalDate dateReceipt;
    private String fullName;
    private Long groupId;
}
