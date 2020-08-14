package ar.com.ml.ibmcloud.projects.mutants.repositories;

import ar.com.ml.ibmcloud.projects.mutants.models.Stats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatsRepository extends JpaRepository<Stats,Long> {
}
