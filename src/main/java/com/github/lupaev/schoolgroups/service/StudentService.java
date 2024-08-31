package com.github.lupaev.schoolgroups.service;

import com.github.lupaev.schoolgroups.dto.StudentDto;
import com.github.lupaev.schoolgroups.mapper.StudentMapper;
import com.github.lupaev.schoolgroups.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    public List<StudentDto> getStudentsByGroup(Long groupId) {
        return studentMapper.toDto(studentRepository.findByGroup_IdOrderByFullNameAsc(groupId));
    }

    public void saveStudent(StudentDto studentDto) {
        studentRepository.save(studentMapper.toEntity(studentDto));
    }

    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }
}
