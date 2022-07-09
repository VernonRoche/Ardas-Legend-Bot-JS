package com.ardaslegends.service;

import com.ardaslegends.data.domain.Player;
import com.ardaslegends.data.repository.PlayerRepository;
import com.ardaslegends.data.service.PlayerService;
import com.ardaslegends.data.service.exceptions.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.PersistenceException;
import java.util.Optional;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
public class AbstractServiceTest {

    PlayerService service;
    PlayerRepository mockRepository;

    @BeforeEach
    void setup() {
        mockRepository = mock(PlayerRepository.class);
        service = new PlayerService(mockRepository, null, null);
    }

    @Test
    void ensureSecureSaveWorksProperly() {
        log.debug("Testing if secureSave works properly with correct values");

        // Assign
        log.trace("Initializing player object to save");
        Player player = Player.builder().discordID("RandomId").build();

        log.trace("Initializing mock methods");
        when(mockRepository.save(player)).thenReturn(player);

        // Act
        log.trace("Executing secureSave");
        var result = service.secureSave(player, mockRepository);

        log.trace("Asserting that the returned player object is the same as the previously initialized one");
        assertThat(result).isEqualTo(player);

        log.info("secureSave works properly with correct values");
    }

    @Test
    void ensureSecureSaveThrowsServiceExceptionOnPersistenceExceptionThrow() {
        log.debug("Testing if secureSave throws ServiceException when PersistenceException is thrown");

        // Assign
        log.trace("Initializing PersistenceException");
        PersistenceException pEx = new PersistenceException("Database down");

        log.trace("Initializing mock method");
        when(mockRepository.save(null)).thenThrow(pEx);

        // Act
        log.trace("Executing secureSave");
        log.trace("Asserting that ServiceException will be thrown");
        var result = assertThrows(ServiceException.class, () -> service.secureSave(null, mockRepository));

        log.trace("Asserting that cause of ServiceException is the previously initialized PersistenceException");
        assertThat(result.getCause()).isEqualTo(pEx);
    }
    @Test
    void ensureSecureFindWorksProperlyAndUnderstandsDifferentFindMethods() {
        log.debug("Testing if secureFind works properly and understands different find() methods");
        // Assign
        log.trace("Initializing return player object");
        Player returnPlayer = Player.builder().discordID("Helloo").build();

        log.trace("Initializing mock methods");
        when(mockRepository.findByDiscordID("1")).thenReturn(Optional.of(returnPlayer));
        when(mockRepository.findPlayerByIgn("2")).thenReturn(Optional.of(returnPlayer));
        when(mockRepository.findPlayerByRpChar("3")).thenReturn(Optional.of(returnPlayer));

        // Act
        log.trace("Executing methods");
        var result1 = service.secureFind("1", mockRepository::findByDiscordID);
        var result2 = service.secureFind("2", mockRepository::findPlayerByIgn);
        var result3 = service.secureFind("3", mockRepository::findPlayerByRpChar);

        // Assert
        log.trace("Asserting that all methods returned correct objects");
        assertThat(result1.get().getDiscordID()).isEqualTo(returnPlayer.getDiscordID());
        assertThat(result2.get().getDiscordID()).isEqualTo(returnPlayer.getDiscordID());
        assertThat(result3.get().getDiscordID()).isEqualTo(returnPlayer.getDiscordID());

        log.info("secureFind works properly with correct valeus");
    }

    @Test
    void ensureSecureFindThrowsServiceExceptionWhenPassedFunctionIsNull() {
        log.debug("Testing if secureFind throws ServiceException when it gets passed a null function");

        // Assign
        log.trace("Initializing variables");
        Function function = null;
        String identifier = "ProbWork";

        // Act
        log.trace("Executing method");
        log.trace("Asserting that method call will throw ServiceException");
        var result = assertThrows(ServiceException.class, () -> service.secureFind(identifier, function));

        log.trace("Asserting that thrown method has correct message");
        assertThat(result.getMessage()).isEqualTo("Passed function on secureFind method is null!");

        log.info("secureFind properly throws ServiceException on passed null function");
    }

    @Test
    void ensureSecureFindThrowsServiceExceptionWhenPersistenceExceptionIsThrown() {
        log.debug("Testing if secureFind throws ServiceException when PersistenceException is thrown");

        // Assign
        log.trace("Initializing identifier");
        String identifier = "RandomDiscId";

        log.trace("Initializing PersistenceExceptioN");
        PersistenceException pEx = new PersistenceException("Database down");

        log.trace("Initializing mock methods");
        when(mockRepository.findByDiscordID(identifier)).thenThrow(pEx);

        // Act
        log.trace("Executing secureFind with findByDiscordId");
        log.trace("Asserting that ServiceException will be thrown");
        var result = assertThrows(ServiceException.class, () -> service.secureFind(identifier, mockRepository::findByDiscordID));

        log.trace("Asserting that cause of ServiceException is previously initialized PersistenceException");
        assertThat(result.getCause()).isEqualTo(pEx);

        log.info("secureFind properly throws ServiceException on PersistenceException throw");
    }
}
