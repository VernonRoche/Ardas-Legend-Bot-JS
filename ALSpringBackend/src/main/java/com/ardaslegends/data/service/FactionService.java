package com.ardaslegends.data.service;

import com.ardaslegends.data.domain.Faction;
import com.ardaslegends.data.repository.FactionRepository;
import com.ardaslegends.data.service.exceptions.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.PersistenceException;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j

@Service
@Transactional(readOnly = true)
public class FactionService {

    private final FactionRepository factionRepository;

    public Faction getFactionByName(String name) {
        log.debug("Fetching Faction with name [{}]", name);
        Objects.requireNonNull(name, "Faction name must not be nulL!");

        if(name.isBlank()) {
            log.warn("Faction name is blank");
            throw new IllegalArgumentException("Faction name must not be blank!");
        }
        Optional<Faction> fetchedFaction = Optional.empty();
        try {
            fetchedFaction = factionRepository.findById(name);
        } catch (PersistenceException pEx) {
            log.warn("Database error when fetching factionRepository.findById. Faction name: {}",name);
            throw ServiceException.cannotReadEntityDueToDatabase(null, pEx);
        }

        
        if (fetchedFaction.isEmpty()) {
            log.warn("No faction found with name {}", name);
            throw new IllegalArgumentException("FactionService, no faction with name %s can be found".formatted(name));
        }
        log.info("Successfully completed fetch from database, result {}", fetchedFaction);

        return fetchedFaction.get();

    }


}
