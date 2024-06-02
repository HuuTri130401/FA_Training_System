package com.fptacademy.training.repository;
import com.fptacademy.training.domain.ClassDetail;
import com.fptacademy.training.domain.enumeration.ClassStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ClassDetailRepository extends JpaRepository<ClassDetail,Long> {

    @Query("select c from ClassDetail c where c.classField.id = :id and c.status <> 'DELETED'")
    Optional<ClassDetail> findDetailsByClass_IdAndStatusNotDeleted(@Param("id") Long class_id);

    @Transactional
    @Modifying
    @Query("update ClassDetail c set c.status = ?1 where c.id = ?2")
    void updateStatusById(String status, Long id);

    @Query(value = " SELECT cd.* FROM class_details cd " +
            " INNER JOIN class_schedules cs " +
            " ON cs.class_detail_id = cd.id " +
            " AND cd.status in ('OPENNING','PLANNING') " +
            " AND cs.study_date = ?1 ", nativeQuery = true)
    List<ClassDetail> findActiveClassByStudyDateAndStatus(LocalDate date);

}
