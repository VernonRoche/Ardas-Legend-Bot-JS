package com.ardaslegends.data.service;

import com.ardaslegends.data.domain.AbstractDomainEntity;
import com.ardaslegends.data.service.exceptions.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.PersistenceException;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Slf4j
public abstract class AbstractService<T extends AbstractDomainEntity, R extends JpaRepository<T, ?>>{



    public <G, F extends AbstractDomainEntity> Optional<F> secureFind(G identifier, Function<G, Optional<F>> func) {
        if (func == null) {
            log.warn("SecureFind Function parameter is null!");
            throw ServiceException.passedNullFunction();
        }
        try {
            return func.apply(identifier);
        } catch (PersistenceException pEx) {
            log.warn("Encountered Database Error while searching for entity, parameter [{}]", identifier);
            throw ServiceException.secureFindFailed(identifier, pEx);
        }
    }

    public <G, F extends AbstractDomainEntity> List<F> secureFindList(G identifier, Function<G, List<F>> func) {
        if (func == null) {
            log.warn("SecureFind Function parameter is null!");
            throw ServiceException.passedNullFunction();
        }
        try {
            return func.apply(identifier);
        } catch (PersistenceException pEx) {
            log.warn("Encountered Database Error while searching for entity, parameter [{}]", identifier);
            throw ServiceException.secureFindFailed(identifier, pEx);
        }
    }

    public T secureSave(T entity, R repository) {
       try {
            return repository.save(entity);
       } catch (PersistenceException pEx) {
            log.warn("Encountered Database Error while saving entity [{}]", entity);
            throw ServiceException.cannotSaveEntity(entity, pEx);
       }
    }

    public void secureDelete(T entity, R repository) {
        try {
            repository.delete(entity);
        } catch (PersistenceException pEx) {
            log.warn("Encountered Database Error while deleting entity[{}]", entity);
            throw ServiceException.cannotSaveEntity(entity, pEx);
        }
    }
}
