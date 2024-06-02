package com.fptacademy.training.domain.enumeration;

public enum RoleName {
    SUPER_ADMIN ("Super Admin"),
    CLASS_ADMIN ("Class Admin"),
    TRAINER ("Trainer"),
    TRAINEE ("Trainee")
    ;

    private final String text;

    RoleName(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
