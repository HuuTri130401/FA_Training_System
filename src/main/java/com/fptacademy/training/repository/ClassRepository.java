package com.fptacademy.training.repository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.fptacademy.training.domain.ClassSchedule;
import com.fptacademy.training.domain.enumeration.ClassStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fptacademy.training.domain.Class;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;

public interface ClassRepository extends JpaRepository<Class, Long>, JpaSpecificationExecutor<Class> {
    List<Class> findByProgram_Id(Long id);
    @Query("select c from Class c where c.id = :id and c.classDetail.status <> 'DELETED'")
    Optional<Class> findByIdAndStatusNotDeleted(@Param("id") Long id);

    static Specification<Class> statusNotDeleted() {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.notEqual(root.get("classDetail").get("status"), ClassStatus.DELETED.toString())
        );
    }
    static Specification<Class> fieldForSearch(String keyword) {
        return ((root, query, criteriaBuilder) -> {
            String pattern = "%" + keyword.toLowerCase() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("code")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("createdAt").as(String.class)), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("createdBy").get("code")), pattern),
                    criteriaBuilder.equal(root.get("duration").as(String.class), keyword),
                     criteriaBuilder.like(criteriaBuilder.lower(root.get("classDetail").get("location").get("city")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("classDetail").get("location").get("fsu")), pattern)
            );
        });
    }

    static Specification<Class> fieldForFilter(List<String> cities,
                                               List<String> statuses,
                                               List<String> attendeeTypes,
                                               String fsu) {
        return ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (cities != null) {
                List<String> lowerCaseCities = cities.stream().map(String::toLowerCase).toList();
                predicates.add(
                        criteriaBuilder.lower(root.get("classDetail").get("location").get("city"))
                                .in(lowerCaseCities)
                );
            }
            if (statuses != null) {
                List<String> lowerCaseStatuses = statuses.stream().map(String::toLowerCase).toList();
                predicates.add(
                        criteriaBuilder.lower(root.get("classDetail").get("status"))
                                .in(lowerCaseStatuses)
                );
            }
            if (attendeeTypes != null) {
                List<String> lowerCaseAttendeeTypes = attendeeTypes.stream().map(String::toLowerCase).toList();
                predicates.add(
                        criteriaBuilder.lower(criteriaBuilder.lower(root.get("classDetail").get("attendee").get("type")))
                                .in(lowerCaseAttendeeTypes)
                );
            }
            if (fsu != null) {
                predicates.add(
                        criteriaBuilder.equal(root.get("classDetail").get("location").get("fsu"), fsu)
                );
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }

    static Specification<Class> getSpecificationForSearchAndFilter(List<String> keywords,
                                                          List<String> cities,
                                                          List<String> statuses,
                                                          List<String> attendeeTypes,
                                                          String fsu) {
        Specification<Class> specification = Specification.where(statusNotDeleted());
        if (keywords != null) {
            for (String keyword : keywords)
                specification = specification.and(fieldForSearch(keyword));
        }
        specification = specification.and(fieldForFilter(cities, statuses, attendeeTypes, fsu));
        return specification;
    }
}
