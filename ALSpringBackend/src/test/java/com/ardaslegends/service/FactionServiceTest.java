package com.ardaslegends.service;

import com.ardaslegends.data.domain.Faction;
import com.ardaslegends.data.repository.FactionRepository;
import com.ardaslegends.data.service.FactionService;
import com.ardaslegends.data.service.exceptions.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.PersistenceException;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
public class FactionServiceTest {

    private FactionRepository mockFactionRepository;

    private FactionService factionService;

    @BeforeEach
    void setup() {
        mockFactionRepository = mock(FactionRepository.class);
        factionService = new FactionService(mockFactionRepository);
    }

    @Test
    void ensureGetByFactionNameWorksProperly() {
        // Assign
        String name = "Mordor";

        when(mockFactionRepository.findById(name)).thenReturn(Optional.of(Faction.builder().name(name).build()));

        // Act
        var result = factionService.getFactionByName(name);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(name);
    }

    @Test
    void ensureGetByFactionNameThrowsIllegalArgumentExceptionWhenNameIsBlank() {
        // Assign
        String name = " ";

        // Assert
        var result = assertThrows(IllegalArgumentException.class, () -> factionService.getFactionByName(name));

        assertThat(result.getMessage()).isEqualTo("Faction name must not be blank!");

    }

    @Test
    void ensureGetByFactionNameThrowsServiceExceptionWhenDatabaseDown() {
        // Assign
        String name = "Mordor";

        PersistenceException pEx = new PersistenceException("Database down");

        when(mockFactionRepository.findById(name)).thenThrow(pEx);

        // Assert
        var result = assertThrows(ServiceException.class, () -> factionService.getFactionByName(name));

        assertThat(result.getCause()).isEqualTo(pEx);
    }

    @Test
    void ensureGetByFactionNameThrowsIAEWhenFetchedFactionIsEmpty() {
        log.debug("Testing if getByFaction in FactionService throws IAE when Fetched Faction is Empty");

        String name = "Mordor";

        when(mockFactionRepository.findById(name)).thenReturn(Optional.empty());

        // Assert
        var result = assertThrows(IllegalArgumentException.class, () -> factionService.getFactionByName(name));
    }

}
