package com.ardaslegends.data.repository;

import com.ardaslegends.data.domain.Army;
import com.ardaslegends.data.domain.ClaimBuild;
import com.ardaslegends.data.service.ArmyService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArmyRepository extends JpaRepository<Army, String> {

    public Optional<Army> findArmyByName(String name);

    int countArmiesByOriginalClaimbuild(ClaimBuild claimBuild);
}
