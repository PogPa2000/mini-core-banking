package com.example.pogpa.mini_core_banking.enums;

public enum GenderType {

    MALE("MALE"),
    FEMALE("FEMALE");

    private final String value;

    GenderType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static GenderType fromValue(String value) {
        for (GenderType type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }

        throw new IllegalArgumentException(
                "Invalid gender value: " + value
        );
    }
}
