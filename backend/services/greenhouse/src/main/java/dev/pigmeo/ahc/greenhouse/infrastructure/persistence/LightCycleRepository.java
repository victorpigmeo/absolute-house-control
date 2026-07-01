package dev.pigmeo.ahc.greenhouse.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LightCycleRepository extends JpaRepository<LightCycle, Long> {}
