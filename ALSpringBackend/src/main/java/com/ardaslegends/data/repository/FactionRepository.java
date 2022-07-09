package com.ardaslegends.data.repository;

import com.ardaslegends.data.domain.Faction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FactionRepository extends JpaRepository<Faction, String> {
}
