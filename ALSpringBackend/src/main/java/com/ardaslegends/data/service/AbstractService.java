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


    /***
     * This method was specifically made for Query Operations of JPA Repository Classes.
     * Wraps the passed method into a try catch block, catching PersistenceException and proper logging
     * @param identifier the identifier, also the parameter of the passed function
     * @param func function that will be used in the operation
     * @return equals the passed function's return type
     */
    public <G, A> A secureFind(G identifier, Function<G, A> func) {
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
