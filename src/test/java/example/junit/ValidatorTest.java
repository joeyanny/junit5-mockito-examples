package example.junit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import example.junit.exceptions.TestNotFoundException;
import example.junit.models.Tests;

public class ValidatorTest {

    private static final String CODE = "test0001";
    private static final String NAME = "Practise test 1";
    private static final int MAX_SCORE = 10;

    private Validator validator;

    @BeforeEach
    public void setUp() {
        Tests tests = new Tests();
        tests.addTest(CODE, NAME, MAX_SCORE);
        validator = new Validator(tests);
    }

    @DisplayName("Valid score is successfully validated")
    @Test
    public void testValid() throws Exception {
        assertTrue(validator.isValidScore(CODE, 5));
    }

    @DisplayName("Exception thrown if score is below 0")
    @Test
    public void testBelowMinInvalid() throws Exception {
        assertFalse(validator.isValidScore(CODE, -1));
    }

    @DisplayName("Exception thrown if score is above max score for the test")
    @Test
    public void testAboveMaxInvalid() throws Exception {
        assertFalse(validator.isValidScore(CODE, 11));
    }

    @DisplayName("Exception thrown if test cannot be found")
    @Test
    public void testErrorTestNotFound() throws Exception {
        assertThrows(TestNotFoundException.class, () -> validator.isValidScore("invalid", 5));
    }
}
