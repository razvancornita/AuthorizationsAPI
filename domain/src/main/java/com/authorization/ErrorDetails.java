package com.authorization;

import lombok.Data;

@Data
public class ErrorDetails {
    private String code;
    private String key;
    private String description;

    public ErrorDetails(String code, String key, String description) {
        this.code = code;
        this.key = key;
        this.description = description;
    }
}
