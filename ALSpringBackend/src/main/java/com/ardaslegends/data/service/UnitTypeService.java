package com.ardaslegends.data.service;

import com.ardaslegends.data.domain.UnitType;
import com.ardaslegends.data.repository.UnitTypeRepository;
import com.ardaslegends.data.service.utils.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j

@Service
@Transactional(readOnly = true)
public class UnitTypeService extends AbstractService<UnitType, UnitTypeRepository> {

    private final UnitTypeRepository unitTypeRepository;

    public UnitType getUnitTypeByName(String name) {
        log.debug("Getting UnitType with name [{}]", name);

        ServiceUtils.checkAllNulls(name);
        ServiceUtils.checkAllBlanks(name);

        log.debug("Fetching unit with name [{}]", name);
        Optional<UnitType> fetchedUnitType = secureFind(name, unitTypeRepository::findById);

        if(fetchedUnitType.isEmpty()) {
            log.warn("No unitType found with name [{}]", name);
            throw new IllegalArgumentException("No Unit found with name %s".formatted(name));
        }

        log.info("Successfully returning Unit with name [{}]", name);
        return fetchedUnitType.get();
    }
}
