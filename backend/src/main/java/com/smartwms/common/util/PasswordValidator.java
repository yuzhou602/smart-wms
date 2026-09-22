package com.smartwms.common.util;

import java.util.regex.Pattern;

public class PasswordValidator {

    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 32;

    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]");

    public static ValidationResult validate(String password) {
        if (password == null || password.isEmpty()) {
            return new ValidationResult(false, "密码不能为空");
        }

        if (password.length() < MIN_LENGTH) {
            return new ValidationResult(false, "密码长度不能少于" + MIN_LENGTH + "个字符");
        }

        if (password.length() > MAX_LENGTH) {
            return new ValidationResult(false, "密码长度不能超过" + MAX_LENGTH + "个字符");
        }

        if (!UPPERCASE_PATTERN.matcher(password).find()) {
            return new ValidationResult(false, "密码必须包含至少一个大写字母");
        }

        if (!LOWERCASE_PATTERN.matcher(password).find()) {
            return new ValidationResult(false, "密码必须包含至少一个小写字母");
        }

        if (!DIGIT_PATTERN.matcher(password).find()) {
            return new ValidationResult(false, "密码必须包含至少一个数字");
        }

        if (!SPECIAL_CHAR_PATTERN.matcher(password).find()) {
            return new ValidationResult(false, "密码必须包含至少一个特殊字符(!@#$%^&*()_+-=[]{};':\"\\|,.<>/?）");
        }

        return new ValidationResult(true, "密码符合要求");
    }

    public static class ValidationResult {
        private final boolean valid;
        private final String message;

        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public boolean isValid() {
            return valid;
        }

        public String getMessage() {
            return message;
        }
    }
}
