package com.smartwms.common.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PasswordValidatorTest {

    @Test
    void testValidPassword() {
        PasswordValidator.ValidationResult result = PasswordValidator.validate("Test@1234");
        assertTrue(result.isValid());
    }

    @Test
    void testEmptyPassword() {
        PasswordValidator.ValidationResult result = PasswordValidator.validate("");
        assertFalse(result.isValid());
        assertEquals("密码不能为空", result.getMessage());
    }

    @Test
    void testNullPassword() {
        PasswordValidator.ValidationResult result = PasswordValidator.validate(null);
        assertFalse(result.isValid());
        assertEquals("密码不能为空", result.getMessage());
    }

    @Test
    void testTooShortPassword() {
        PasswordValidator.ValidationResult result = PasswordValidator.validate("Test@1");
        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("长度不能少于"));
    }

    @Test
    void testNoUppercase() {
        PasswordValidator.ValidationResult result = PasswordValidator.validate("test@1234");
        assertFalse(result.isValid());
        assertEquals("密码必须包含至少一个大写字母", result.getMessage());
    }

    @Test
    void testNoLowercase() {
        PasswordValidator.ValidationResult result = PasswordValidator.validate("TEST@1234");
        assertFalse(result.isValid());
        assertEquals("密码必须包含至少一个小写字母", result.getMessage());
    }

    @Test
    void testNoDigit() {
        PasswordValidator.ValidationResult result = PasswordValidator.validate("Test@abcd");
        assertFalse(result.isValid());
        assertEquals("密码必须包含至少一个数字", result.getMessage());
    }

    @Test
    void testNoSpecialChar() {
        PasswordValidator.ValidationResult result = PasswordValidator.validate("Test12345");
        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("特殊字符"));
    }
}
