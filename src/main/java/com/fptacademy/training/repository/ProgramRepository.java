package com.fptacademy.training.repository;


import com.fptacademy.training.domain.Program;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("NullableProblems")
public interface ProgramRepository extends JpaRepository<Program, Long> {
    @Override
//    @EntityGraph(attributePaths = {"createdBy", "lastModifiedBy", "syllabuses.createdBy"})
//    @EntityGraph(attributePaths = {"createdBy", "lastModifiedBy", "syllabuses", "syllabuses.sessions", "syllabuses.sessions.units"})
    //@Query("SELECT DISTINCT p FROM Program p JOIN FETCH p.syllabuses s JOIN FETCH s.sessions ss JOIN FETCH ss.units WHERE p.id = :id")
    Optional<Program> findById(@Param("id") Long id);

    @Override
//    @EntityGraph(value = "graph.Program.syllabus.session")
    List<Program> findAll();

//    @EntityGraph(value = "graph.Program.syllabus.session")
    List<Program> findByActivated(Boolean activated);

    boolean existsByName(String name);

//    @EntityGraph(attributePaths = {"createdBy", "lastModifiedBy", "syllabuses.createdBy", "syllabuses.sessions.units"})
    Optional<Program> findByName(String name);

//    @EntityGraph(value = "graph.Program.syllabus.session")
    List<Program> findByNameContainsIgnoreCaseOrCreatedBy_FullNameContainsIgnoreCase(String name, String fullName);

//    @EntityGraph(value = "graph.Program.syllabus.session")
    List<Program> findByNameContainsIgnoreCaseOrCreatedBy_FullNameContainsIgnoreCaseAndActivated(String name, String fullName, Boolean activated);

    @Query("select p from Program p where p.id = :id")
    Optional<Program> findByIdForClass(@Param("id") Long id);
}
