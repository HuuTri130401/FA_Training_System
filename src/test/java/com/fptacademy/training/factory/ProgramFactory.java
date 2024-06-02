package com.fptacademy.training.factory;

import com.fptacademy.training.domain.Program;
import com.fptacademy.training.domain.Syllabus;
import com.github.javafaker.Faker;

import java.util.ArrayList;
import java.util.List;

public class ProgramFactory {
    public static Program createDummyProgram() {
        Faker faker = new Faker();
        List<Syllabus> syllabuses = new ArrayList<>();
        for (int i = 0; i < faker.random().nextInt(2, 3); ++i) {
            syllabuses.add(SyllabusFactory.createDummySyllabus());
        }
        return Program.builder()
                .name(faker.educator().course())
                .syllabuses(syllabuses)
                .build();
    }

    public static Program createDummyProgram(List<Syllabus> syllabuses) {
        Faker faker = new Faker();
        return Program.builder()
                .name(faker.educator().course())
                .syllabuses(syllabuses)
                .build();
    }
}
