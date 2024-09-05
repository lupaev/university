package com.github.lupaev.university.service;

import com.github.lupaev.university.dto.StudentDto;
import com.github.lupaev.university.entity.Student;
import com.github.lupaev.university.mapper.StudentMapper;
import com.github.lupaev.university.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentMapper studentMapper;

    @InjectMocks
    private StudentService studentService;

    private Student student;

    private StudentDto studentDto;

    @BeforeEach
    public void setUp() {
        student = new Student();
        student.setId(1L);
        student.setFullName("John Doe");

        studentDto = new StudentDto();
        studentDto.setId(1L);
        studentDto.setFullName("John Doe");
    }

    @Test
    public void testGetStudentsByGroup() {
        Long groupId = 1L;
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);

        Page<Student> studentPage = new PageImpl<>(Collections.singletonList(student));

        when(studentRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(studentPage);
        when(studentMapper.toDto(student)).thenReturn(studentDto);

        List<StudentDto> result = studentService.getStudentsByGroup(groupId, page, size);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getFullName());

        verify(studentRepository, times(1)).findAll(any(Specification.class), eq(pageable));
        verify(studentMapper, times(1)).toDto(student);
    }

    @Test
    public void testSaveStudent() {
        when(studentMapper.toEntity(studentDto)).thenReturn(student);
        when(studentRepository.save(student)).thenReturn(student);
        when(studentMapper.toDto(student)).thenReturn(studentDto);

        StudentDto result = studentService.saveStudent(studentDto);

        assertNotNull(result);
        assertEquals("John Doe", result.getFullName());

        verify(studentRepository, times(1)).save(student);
        verify(studentMapper, times(1)).toEntity(studentDto);
        verify(studentMapper, times(1)).toDto(student);
    }

    @Test
    public void testDeleteStudent() {
        Long studentId = 1L;

        doNothing().when(studentRepository).deleteById(studentId);

        studentService.deleteStudent(studentId);

        verify(studentRepository, times(1)).deleteById(studentId);
    }
}
