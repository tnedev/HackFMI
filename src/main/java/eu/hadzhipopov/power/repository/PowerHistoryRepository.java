package eu.hadzhipopov.power.repository;

import eu.hadzhipopov.power.domain.PowerHistory;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the PowerHistory entity.
 */
public interface PowerHistoryRepository extends JpaRepository<PowerHistory,Long> {

}
