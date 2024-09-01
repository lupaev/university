package com.github.lupaev.schoolgroups.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentDto {
    private Long id;
    private String dateReceipt;
    private String fullName;
    private Long groupId;
}
