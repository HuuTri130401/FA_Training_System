package com.fptacademy.training.repository;

import com.fptacademy.training.domain.ClassDetail;
import com.fptacademy.training.domain.ClassSchedule;
import com.fptacademy.training.domain.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public interface ClassScheduleRepository extends JpaRepository<ClassSchedule, Long>, JpaSpecificationExecutor<ClassSchedule> {

    @Query(value = "SELECT COALESCE(row_num, 0) " +
            " FROM ( " +
            "    SELECT *, ROW_NUMBER() OVER (ORDER BY study_date) as row_num " +
            "    FROM class_schedules " +
            "    WHERE class_detail_id = ?1 " +
            "    ORDER BY study_date " +
            ") t " +
            " WHERE t.id = ?2 ", nativeQuery = true)
    Integer getCurrentClassDayOfClassSchedule(Long classDetailId, Long classScheduleId);

    default List<ClassSchedule> findFilterActiveClassByStudyDateBetween(LocalDate startDate, LocalDate endDate, String status, Long userId, List<String> className, List<String> classCode, List<String> city) {
        Specification<ClassSchedule> spec = Specification.where((root, query, criteriaBuilder) -> {
            query.distinct(true);
            Join<ClassSchedule, ClassDetail> classDetailJoin = root.join("classDetail");
            Join<ClassDetail, User> userJoin = classDetailJoin.join("users");

            Predicate activatedUserPredicate = criteriaBuilder.isTrue(userJoin.get("activated"));
            Predicate statusPredicate = criteriaBuilder.equal(classDetailJoin.get("status"), status);
            Predicate studyDatePredicate = criteriaBuilder.between(root.get("studyDate"), startDate, endDate);
            Predicate userIdPredicate = userId != null ? criteriaBuilder.equal(userJoin.get("id"), userId) : criteriaBuilder.conjunction();

            Predicate classNamePredicate = criteriaBuilder.conjunction();
            if (className != null) {
                List<Predicate> classNamePredicates = new ArrayList<>();
                for (String name : className) {
                    classNamePredicates.add(criteriaBuilder.like(classDetailJoin.get("classField").get("name"), "%" + name + "%"));
                }
                classNamePredicate = criteriaBuilder.or(classNamePredicates.toArray(new Predicate[0]));
            }

            Predicate classCodePredicate = criteriaBuilder.conjunction();
            if (classCode != null) {
                List<Predicate> classCodePredicates = new ArrayList<>();
                for (String code : classCode) {
                    classCodePredicates.add(criteriaBuilder.like(classDetailJoin.get("classField").get("code"), "%" + code + "%"));
                }
                classCodePredicate = criteriaBuilder.or(classCodePredicates.toArray(new Predicate[0]));
            }

            Predicate cityPredicate = criteriaBuilder.conjunction();
            if (city != null) {
                List<Predicate> cityPredicates = new ArrayList<>();
                for (String c : city) {
                    cityPredicates.add(criteriaBuilder.like(classDetailJoin.get("location").get("city"), "%" + c + "%"));
                }
                cityPredicate = criteriaBuilder.or(cityPredicates.toArray(new Predicate[0]));
            }

            return criteriaBuilder.and(activatedUserPredicate, statusPredicate, studyDatePredicate, userIdPredicate, classNamePredicate, classCodePredicate, cityPredicate);
        });

        return this.findAll(spec, Sort.by("studyDate"));
    }

    default List<ClassSchedule> findFilterActiveClassByStudyDate(LocalDate date, String status, Long userId, List<String> className, List<String> classCode, List<String> city) {
        Specification<ClassSchedule> spec = Specification.where((root, query, criteriaBuilder) -> {
            query.distinct(true);
            Join<ClassSchedule, ClassDetail> classDetailJoin = root.join("classDetail");
            Join<ClassDetail, User> userJoin = classDetailJoin.join("users");

            Predicate activatedUserPredicate = criteriaBuilder.isTrue(userJoin.get("activated"));
            Predicate statusPredicate = criteriaBuilder.equal(classDetailJoin.get("status"), status);
            Predicate studyDatePredicate = criteriaBuilder.equal(root.get("studyDate"), date);
            Predicate userIdPredicate = userId != null ? criteriaBuilder.equal(userJoin.get("id"), userId) : criteriaBuilder.conjunction();

            Predicate classNamePredicate = criteriaBuilder.conjunction();
            if (className != null) {
                List<Predicate> classNamePredicates = new ArrayList<>();
                for (String name : className) {
                    classNamePredicates.add(criteriaBuilder.like(classDetailJoin.get("classField").get("name"), "%" + name + "%"));
                }
                classNamePredicate = criteriaBuilder.or(classNamePredicates.toArray(new Predicate[0]));
            }

            Predicate classCodePredicate = criteriaBuilder.conjunction();
            if (classCode != null) {
                List<Predicate> classCodePredicates = new ArrayList<>();
                for (String code : classCode) {
                    classCodePredicates.add(criteriaBuilder.like(classDetailJoin.get("classField").get("code"), "%" + code + "%"));
                }
                classCodePredicate = criteriaBuilder.or(classCodePredicates.toArray(new Predicate[0]));
            }

            Predicate cityPredicate = criteriaBuilder.conjunction();
            if (city != null) {
                List<Predicate> cityPredicates = new ArrayList<>();
                for (String c : city) {
                    cityPredicates.add(criteriaBuilder.like(classDetailJoin.get("location").get("city"), "%" + c + "%"));
                }
                cityPredicate = criteriaBuilder.or(cityPredicates.toArray(new Predicate[0]));
            }

            return criteriaBuilder.and(activatedUserPredicate, statusPredicate, studyDatePredicate, userIdPredicate, classNamePredicate, classCodePredicate, cityPredicate);

        });

        return this.findAll(spec, Sort.by("studyDate"));
    }


}
