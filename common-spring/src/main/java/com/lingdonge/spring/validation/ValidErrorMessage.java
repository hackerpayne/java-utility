package com.lingdonge.spring.validation;

import lombok.Data;

@Data
public class ValidErrorMessage {

    private String propertyPath;

    private String message;

    public ValidErrorMessage() {
    }

    public ValidErrorMessage(String propertyPath, String message) {
        this.propertyPath = propertyPath;
        this.message = message;
    }
}
