package example.junit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import example.junit.exceptions.InvalidScoreException;
import example.junit.exceptions.TestNotFoundException;
import example.junit.models.ResultType;
import example.junit.models.Tests;

public class ResultCalculatorTest {

    private static final String CODE = "test0001";
    private static final String NAME = "Practise test 1";
    private static final int MAX_SCORE = 10;

    private ResultCalculator calculator;

    @BeforeEach
    public void setUp() {
        Tests tests = new Tests();
        tests.addTest(CODE, NAME, MAX_SCORE);
        calculator = new ResultCalculator(tests);
    }

    @DisplayName("Test pass result")
    @Test
    public void testPass() throws Exception {
        assertEquals(ResultType.PASS, calculator.computeResult(CODE, 8));
    }

    @DisplayName("Test fail result")
    @Test
    public void testFail() throws Exception {
        assertEquals(ResultType.FAIL, calculator.computeResult(CODE, 6));
    }

    @DisplayName("Exception thrown if score is below 0")
    @Test
    public void testBelowMinInvalid() throws Exception {
        assertThrows(InvalidScoreException.class, () -> calculator.computeResult(CODE, -1));
    }

    @DisplayName("Exception thrown if score is above max score for the test")
    @Test
    public void testAboveMaxInvalid() throws Exception {
        assertThrows(InvalidScoreException.class, () -> calculator.computeResult(CODE, 11));
    }

    @DisplayName("Exception thrown if test cannot be found")
    @Test
    public void testErrorTestNotFound() throws Exception {
        assertThrows(TestNotFoundException.class, () -> calculator.computeResult("invalid", -1));
    }

    @DisplayName("Test correct result computed on repeated test runs")
    @RepeatedTest(MAX_SCORE)
    public void testRepeatedScores(RepetitionInfo repetitionInfo) throws Exception {
        int score = repetitionInfo.getCurrentRepetition();
        int percentage = (score * 100) / repetitionInfo.getTotalRepetitions();
        ResultType resultType = percentage >= calculator.getPassPercentage() ? ResultType.PASS : ResultType.FAIL;
        assertEquals(resultType, calculator.computeResult(CODE, score));
    }

    @DisplayName("Test correct result computed in parameterised test")
    @ParameterizedTest
    @MethodSource("getResultCalculatorTestData")
    public void testCorrectResult(int score, ResultType expectedResult) throws Exception {
        assertEquals(expectedResult, calculator.computeResult(CODE, score));
    }

    /**
     * Helper method to create test data
     * 
     * @return data
     */
    static Stream<Arguments> getResultCalculatorTestData() {
        return Stream.of(
            arguments(0, ResultType.FAIL),
            arguments(6, ResultType.FAIL),
            arguments(7, ResultType.PASS),
            arguments(10, ResultType.PASS)
        );
    }
}
