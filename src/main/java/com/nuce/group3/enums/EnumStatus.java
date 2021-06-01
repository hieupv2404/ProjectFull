package com.nuce.group3.enums;

public enum EnumStatus {
    VALID("VALID"),
    INVALID("INVALID");

    private final String name;

    EnumStatus(String status) {
        name = status;
    }
}
