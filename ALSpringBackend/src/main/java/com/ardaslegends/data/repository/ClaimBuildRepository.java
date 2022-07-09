package com.ardaslegends.data.repository;

import com.ardaslegends.data.domain.ClaimBuild;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClaimBuildRepository extends JpaRepository<ClaimBuild, String> {
}
