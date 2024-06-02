package com.fptacademy.training.factory;

import com.fptacademy.training.domain.*;
import com.fptacademy.training.domain.enumeration.SyllabusStatus;
import com.github.javafaker.Faker;

import java.util.ArrayList;
import java.util.List;

public class SyllabusFactory {
    public static Syllabus createDummySyllabus() {
        Faker faker = new Faker();
        int numberOfSessions = faker.random().nextInt(10, 35);
        Syllabus syllabus = Syllabus.builder()
                .name(faker.educator().course())
                .duration(numberOfSessions)
                .code(faker.random().hex(6).toUpperCase())
                .build();
        List<Session> sessions = new ArrayList<>();
        for (int i = 0; i < numberOfSessions; ++i) {
            int numberOfUnits = faker.random().nextInt(1, 3);
            Session session = Session.builder()
                    .syllabus(syllabus)
                    .build();
            List<Unit> units = new ArrayList<>();
            for (int j = 0; j < numberOfUnits; ++j) {
                Unit unit = Unit.builder()
                        .session(session)
                        .totalDurationLesson(faker.number().randomDouble(2, 0, 5))
                        .build();
                int numberOfLessons = faker.random().nextInt(2, 4);
                List<Lesson> lessons = new ArrayList<>();
                for (int k = 0; k < numberOfLessons; ++k) {
                    Lesson lesson = Lesson.builder()
                            .unit(unit)
                            .duration(faker.number().numberBetween(30, 120))
                            .build();
                    List<Material> materials = new ArrayList<>();
                    for (int t = 0; t < faker.random().nextInt(1, 3); ++t) {
                        Material material = Material.builder()
                                .lesson(lesson)
                                .build();
                        materials.add(material);
                    }
                    lesson.setMaterials(materials);
                    lessons.add(lesson);
                }
                unit.setLessons(lessons);
                units.add(unit);
            }
            session.setUnits(units);
            sessions.add(session);
        }
        syllabus.setSessions(sessions);
        return syllabus;
    }
    public static Syllabus createActivatedDummySyllabus() {
        Syllabus syllabus = createDummySyllabus();
        syllabus.setStatus(SyllabusStatus.ACTIVATED);
        return syllabus;
    }
}
