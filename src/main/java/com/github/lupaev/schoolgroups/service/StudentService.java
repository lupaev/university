package com.github.lupaev.schoolgroups.service;

import com.github.lupaev.schoolgroups.dto.StudentDto;
import com.github.lupaev.schoolgroups.entity.Student;
import com.github.lupaev.schoolgroups.mapper.StudentMapper;
import com.github.lupaev.schoolgroups.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.github.lupaev.schoolgroups.specification.StudentSpecification.hasGroupIdAndOrderByFullNameAsc;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    public List<StudentDto> getStudentsByGroup(Long groupId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Student> students = studentRepository.findAll(hasGroupIdAndOrderByFullNameAsc(groupId), pageable);

        return students.stream().map(studentMapper::toDto).toList();
    }

    @Transactional
    public StudentDto saveStudent(StudentDto studentDto) {
        Student saved = studentRepository.save(studentMapper.toEntity(studentDto));
        return studentMapper.toDto(saved);
    }

    @Transactional
    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }
}
