package com.ardaslegends.data.repository;

import com.ardaslegends.data.domain.UnitType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnitTypeRepository extends JpaRepository<UnitType, String> {
}
