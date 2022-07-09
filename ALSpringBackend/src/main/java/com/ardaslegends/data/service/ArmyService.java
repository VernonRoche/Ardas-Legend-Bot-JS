package com.ardaslegends.data.service;

import com.ardaslegends.data.domain.Army;
import com.ardaslegends.data.repository.ArmyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional(readOnly = true)
public class ArmyService extends AbstractService<Army, ArmyRepository> {

    private final ArmyRepository armyRepository;
}
