package com.example.musictheory.models;

import lombok.Getter;

@Getter
public enum Proficiency {

    BEGINNER("Beginner"),
    INTERMEDIATE("Intermediate"),
    PROFICIENT("Proficient"),
    EXPERT("Expert");

    private final String proficiency;


    Proficiency(String proficiency) {
        this.proficiency = proficiency;
    }

    @Override
    public String toString() {
        return "proficiency{" +
                "proficiency='" + proficiency + '\'' +
                '}';
    }
}
