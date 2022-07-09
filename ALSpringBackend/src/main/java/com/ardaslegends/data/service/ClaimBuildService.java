package com.ardaslegends.data.service;

import com.ardaslegends.data.domain.ClaimBuild;
import com.ardaslegends.data.domain.UnitType;
import com.ardaslegends.data.repository.ClaimBuildRepository;
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
public class ClaimBuildService extends AbstractService<ClaimBuild, ClaimBuildRepository> {

    private final ClaimBuildRepository claimbuildRepository;

    public ClaimBuild getClaimBuildByName(String name) {
        log.debug("Getting Claimbuild with name [{}]", name);

        ServiceUtils.checkAllNulls(name);
        ServiceUtils.checkAllBlanks(name);

        log.debug("Fetching unit with name [{}]", name);
        Optional<ClaimBuild> fetchedBuild = secureFind(name, claimbuildRepository::findById);

        if(fetchedBuild.isEmpty()) {
            log.warn("No Claimbuild found with name [{}]", name);
            throw new IllegalArgumentException("No Claimbuild found with name %s".formatted(name));
        }

        log.info("Successfully returning Claimbuild with name [{}]", name);
        return fetchedBuild.get();
    }

}
