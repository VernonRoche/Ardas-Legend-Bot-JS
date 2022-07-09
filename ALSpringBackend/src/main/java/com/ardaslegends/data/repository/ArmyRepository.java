package com.ardaslegends.data.repository;

import com.ardaslegends.data.domain.Army;
import com.ardaslegends.data.service.ArmyService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArmyRepository extends JpaRepository<Army, String> {
}
