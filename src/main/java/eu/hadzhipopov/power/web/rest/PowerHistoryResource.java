package eu.hadzhipopov.power.web.rest;

import com.codahale.metrics.annotation.Timed;
import eu.hadzhipopov.power.domain.PowerHistory;
import eu.hadzhipopov.power.repository.PowerHistoryRepository;
import eu.hadzhipopov.power.web.rest.util.HeaderUtil;
import eu.hadzhipopov.power.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing PowerHistory.
 */
@RestController
@RequestMapping("/api")
public class PowerHistoryResource {

    private final Logger log = LoggerFactory.getLogger(PowerHistoryResource.class);
        
    @Inject
    private PowerHistoryRepository powerHistoryRepository;
    
    /**
     * POST  /powerHistorys -> Create a new powerHistory.
     */
    @RequestMapping(value = "/powerHistorys",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<PowerHistory> createPowerHistory(@RequestBody PowerHistory powerHistory) throws URISyntaxException {
        log.debug("REST request to save PowerHistory : {}", powerHistory);
        if (powerHistory.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("powerHistory", "idexists", "A new powerHistory cannot already have an ID")).body(null);
        }
        PowerHistory result = powerHistoryRepository.save(powerHistory);
        return ResponseEntity.created(new URI("/api/powerHistorys/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("powerHistory", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /powerHistorys -> Updates an existing powerHistory.
     */
    @RequestMapping(value = "/powerHistorys",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<PowerHistory> updatePowerHistory(@RequestBody PowerHistory powerHistory) throws URISyntaxException {
        log.debug("REST request to update PowerHistory : {}", powerHistory);
        if (powerHistory.getId() == null) {
            return createPowerHistory(powerHistory);
        }
        PowerHistory result = powerHistoryRepository.save(powerHistory);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("powerHistory", powerHistory.getId().toString()))
            .body(result);
    }

    /**
     * GET  /powerHistorys -> get all the powerHistorys.
     */
    @RequestMapping(value = "/powerHistorys",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<PowerHistory>> getAllPowerHistorys(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of PowerHistorys");
        Page<PowerHistory> page = powerHistoryRepository.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/powerHistorys");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /powerHistorys/:id -> get the "id" powerHistory.
     */
    @RequestMapping(value = "/powerHistorys/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<PowerHistory> getPowerHistory(@PathVariable Long id) {
        log.debug("REST request to get PowerHistory : {}", id);
        PowerHistory powerHistory = powerHistoryRepository.findOne(id);
        return Optional.ofNullable(powerHistory)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /powerHistorys/:id -> delete the "id" powerHistory.
     */
    @RequestMapping(value = "/powerHistorys/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deletePowerHistory(@PathVariable Long id) {
        log.debug("REST request to delete PowerHistory : {}", id);
        powerHistoryRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("powerHistory", id.toString())).build();
    }
}
