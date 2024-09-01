package com.github.lupaev.schoolgroups.specification;

import com.github.lupaev.schoolgroups.entity.Student;
import org.springframework.data.jpa.domain.Specification;

public class StudentSpecification {
    private StudentSpecification() {}

    public static Specification<Student> hasGroupIdAndOrderByFullNameAsc(Long groupId) {
        return (root, query, cb) -> {
            query.orderBy(cb.asc(root.get("fullName")));
            return cb.equal(root.get("group").get("id"), groupId);
        };
    }
}
