package com.fptacademy.training.repository;

import com.fptacademy.training.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = {"role"})
    Optional<User> findByEmail(String email);

    @Query("Select c from User c where c.role.name = 'Trainer'")
    List<User> findAllTrainers();

    @Query("Select c from User c where c.role.name = 'Class Admin'")
    List<User> findAllClassAdmin();


    boolean existsByEmail(String email);

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByCode(String code);

    Page<User> findUserByActivatedIsTrue(Pageable pageable);

    List<User> findByFullNameContaining(String keyword);

    @Query("SELECT u FROM User u " +
            "LEFT JOIN u.level l " +
            "WHERE (:emailParam IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :emailParam, '%'))) " +
            "AND (:fullNameParam IS NULL OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :fullNameParam, '%'))) " +
            "AND (:codeParam IS NULL OR LOWER(u.code) LIKE LOWER(CONCAT('%', :codeParam, '%'))) " +
            "AND (:levelNameParam IS NULL OR LOWER(l.name) LIKE LOWER(CONCAT('%', :levelNameParam, '%'))) " +
            "AND (:roleNameParam IS NULL OR LOWER(u.role.name) LIKE LOWER(CONCAT('%', :roleNameParam, '%'))) " +
            "AND (:activatedParam IS NULL OR u.activated = :activatedParam) " +
            "AND (:birthdayFromParam IS NULL OR :birthdayToParam IS NULL OR (:birthdayFromParam <= u.birthday AND u.birthday <= :birthdayToParam)) " +
            "AND (:statusParam IS NULL OR LOWER(u.status) LIKE LOWER(CONCAT('%', :statusParam, '%')))")
    Page<User> findByFilters(@Param("emailParam") String email, @Param("fullNameParam") String fullName,
                             @Param("codeParam") String code, @Param("levelNameParam") String levelName,
                             @Param("roleNameParam") String roleName, @Param("activatedParam") Boolean activated,
                             @Param("birthdayFromParam") LocalDate birthdayFrom, @Param("birthdayToParam") LocalDate birthdayTo,
                             @Param("statusParam") String status, Pageable pageable);

    @Query("SELECT u FROM ClassDetail cd " +
            " JOIN cd.users u " +
            " WHERE u.activated = true " +
            " AND cd.id = :classDetailId " +
            " AND u.role.name = :roleName")
    List<User> findMemberOfClassByRole(@Param("classDetailId") Long classDetailId,
                                       @Param("roleName") String roleName);
}