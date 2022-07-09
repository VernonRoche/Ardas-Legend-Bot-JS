package com.ardaslegends.presentation;

import com.ardaslegends.data.domain.Player;
import com.ardaslegends.data.presentation.AbstractRestController;
import com.ardaslegends.data.presentation.exceptions.BadArgumentException;
import com.ardaslegends.data.presentation.exceptions.InternalServerException;
import com.ardaslegends.data.service.exceptions.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@Slf4j
public class AbstractRestControllerTest {


    Function<Integer, Player> mockFunction;
    AbstractRestController abstractController;

    @BeforeEach
    void setup() {
        mockFunction = mock(Function.class);
        abstractController = mock(AbstractRestController.class, CALLS_REAL_METHODS);
    }

    @Test
    void ensureWrappedServiceExecutionWorksProperly() {

        log.debug("Testing if wrappedServiceExecution works with correct values");

        // Assign
        log.trace("Initializing player object");
        Player player = Player.builder().discordID("Woohoo").build();

        log.trace("Parameter for function");
        Integer integer = 5;

        log.trace("Initializing mock method");
        when(mockFunction.apply(integer)).thenReturn(player);

        // Act
        log.trace("Executing wrappedServiceExecution");
        var result = abstractController.wrappedServiceExecution(integer, mockFunction);

        // Assert
        log.trace("Asserting that the player object returned by the serviceExecution equals the previously initialized player object");
        assertThat(result).isEqualTo(player);

        log.info("WrappedServiceExecution works correctly with correct values");
    }

    @Test
    void ensureWrappedServiceExecutionThrowsInternalServerErrorWhenPassedFunctionIsNull() {

        log.debug("Testing if wrappedServiceExecution correctly throws InternalServerError on passed null function");

        // Assign
        log.trace("Initializing parameter");
        Integer parameter = 5;

        // Act / Assert
        log.trace("Executing wrappedServiceExecution");
        log.trace("Asserting that InternalServerError will be thrown");
        var result = assertThrows(InternalServerException.class, () -> abstractController.wrappedServiceExecution(parameter, null));

        log.trace("Asserting that message in exception is correct");
        assertThat(result.getMessage()).isEqualTo("Passed Null Function in wrappedServiceExecution");

        log.info("WrappedServiceExecution correctly throws InternalServerError on passed null function");
    }

    @Test
    void ensureWrappedServiceExecutionThrowsBadArgumentRequestOnNullPointerInFunction() {
        log.debug("Testing if wrappedServiceExecution correctly throws BadArgumentRequest");

        // Assign
        log.trace("Initializing parameter for function");
        Integer parameter = 5;

        log.trace("Initializing NPE object");
        NullPointerException npe = new NullPointerException("Some null value");

        log.trace("Initializing mock methods");
        when(mockFunction.apply(parameter)).thenThrow(npe);

        // Assert
        log.trace("Executing wrappedServiceExecution");
        log.trace("Asserting that method execution throws BadArgumentRequest");
        var result = assertThrows(BadArgumentException.class, () -> abstractController.wrappedServiceExecution(parameter, mockFunction));

        log.trace("Asserting that the cause of the exception is the previously initialized npe object");
        assertThat(result.getCause()).isEqualTo(npe);
        log.info("WrappedServiceExecution correctly throws BadRequest on npe");
    }
    @Test
    void ensureWrappedServiceExecutionThrowsInternalServerErrorOnServiceExceptionInFunction() {
        log.debug("Testing if wrappedServiceExecution correctly throws InternalServerError");

        // Assign
        log.trace("Initializing parameter for function");
        Integer parameter = 5;

        log.trace("Initializing ServiceException object");
        ServiceException se = ServiceException.passedNullFunction();

        log.trace("Initializing mock methods");
        when(mockFunction.apply(parameter)).thenThrow(se);

        // Assert
        log.trace("Executing wrappedServiceExecution");
        log.trace("Asserting that method execution throws InternalServerError");
        var result = assertThrows(InternalServerException.class, () -> abstractController.wrappedServiceExecution(parameter, mockFunction));

        log.trace("Asserting that the cause of the exception is the previously initialized service exception object");
        assertThat(result.getCause()).isEqualTo(se);
        log.info("WrappedServiceExecution correctly throws InternalServerError on ServiceException");
    }
}
