package com.github.lupaev.schoolgroups.repository;

import com.github.lupaev.schoolgroups.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    @Query("SELECT COUNT(s) FROM Student s WHERE s.group.id = :groupId")
    Long countStudentsByGroupId(@Param("groupId") Long groupId);

    List<Group> findAllByOrderByCreatedAtAsc();
}
