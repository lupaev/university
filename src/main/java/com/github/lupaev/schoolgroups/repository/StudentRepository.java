package com.github.lupaev.schoolgroups.repository;

import com.github.lupaev.schoolgroups.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findByGroup_IdOrderByFullNameAsc(Long groupId);
}
