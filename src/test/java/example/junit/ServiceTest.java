package example.junit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.SQLException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import example.junit.exceptions.InvalidScoreException;
import example.junit.exceptions.TestNotFoundException;
import example.junit.models.Result;
import example.junit.models.ResultType;

@ExtendWith(MockitoExtension.class)
public class ServiceTest {

    private static final String CODE = "test0001";
    private static final int SCORE = 9;
    private static final ResultType RESULT_TYPE = ResultType.PASS;

    @Mock
    private Validator validator;

    @Mock
    private ResultCalculator calculator;

    @Mock
    private DAO dao;

    @InjectMocks
    private Service service;

    @DisplayName("Result is computed and stored successfully")
    @Test
    public void testResultStored() throws Exception {
        when(validator.isValidScore(CODE, SCORE)).thenReturn(true);
        when(calculator.computeResult(CODE, SCORE)).thenReturn(RESULT_TYPE);
        doNothing().when(dao).storeResult(any(Result.class));

        service.computeAndStoreResult(CODE, SCORE);

        verify(validator).isValidScore(CODE, SCORE);
        verify(calculator).computeResult(CODE, SCORE);
        verify(dao).storeResult(any(Result.class));
    }

    @DisplayName("Exception occurs when validating the score")
    @Test
    public void testValidationError() throws Exception {
        when(validator.isValidScore(CODE, SCORE)).thenThrow(new TestNotFoundException("unit test error"));
        assertThrows(TestNotFoundException.class, () -> service.computeAndStoreResult(CODE, SCORE));
    }

    @DisplayName("Exception occurs when computing the result")
    @Test
    public void testResultCalulatorError() throws Exception {
        when(validator.isValidScore(CODE, SCORE)).thenReturn(true);
        when(calculator.computeResult(CODE, SCORE)).thenThrow(new InvalidScoreException("unit test error"));
        assertThrows(InvalidScoreException.class, () -> service.computeAndStoreResult(CODE, SCORE));
    }

    @DisplayName("Exception occurs when storing the result")
    @Test
    public void testDAOStoreError() throws Exception {
        when(validator.isValidScore(CODE, SCORE)).thenReturn(true);
        when(calculator.computeResult(CODE, SCORE)).thenReturn(RESULT_TYPE);

        // Must use doThrow().when() instead of when().thenThrow() for mocking exceptions in methods that have a void return
        doThrow(new SQLException("unit test error")).when(dao).storeResult(any(Result.class));

        assertThrows(SQLException.class, () -> service.computeAndStoreResult(CODE, SCORE));
    }

    @DisplayName("Verify data in the result is correct")
    @Test
    public void verfyCorrectDataInStoredResult() throws Exception {
        when(validator.isValidScore(CODE, SCORE)).thenReturn(true);
        when(calculator.computeResult(CODE, SCORE)).thenReturn(RESULT_TYPE);

        // Argument captor can capture inputs into mocked methods to be able to verify data created internally in the class under test
        ArgumentCaptor<Result> captor = ArgumentCaptor.forClass(Result.class);
        doNothing().when(dao).storeResult(captor.capture());

        service.computeAndStoreResult(CODE, SCORE);

        Result result = captor.getValue();
        assertEquals(CODE, result.getCode());
        assertEquals(ResultType.PASS, result.getResult());
    }
}
