package com.github.lupaev.schoolgroups.specification;

import com.github.lupaev.schoolgroups.entity.Group;
import org.springframework.data.jpa.domain.Specification;

public class GroupSpecification {
    private GroupSpecification() {}

    public static Specification<Group> orderByCreatedAtAsc() {
        return (root, query, cb) -> {
            query.orderBy(cb.asc(root.get("createdAt")));
            return query.getRestriction();
        };
    }
}
