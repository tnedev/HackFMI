package eu.hadzhipopov.power.web.rest;

import eu.hadzhipopov.power.Application;
import eu.hadzhipopov.power.domain.PowerHistory;
import eu.hadzhipopov.power.repository.PowerHistoryRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the PowerHistoryResource REST controller.
 *
 * @see PowerHistoryResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class PowerHistoryResourceIntTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneId.of("Z"));


    private static final ZonedDateTime DEFAULT_TIMESTAMP = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_TIMESTAMP = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_TIMESTAMP_STR = dateTimeFormatter.format(DEFAULT_TIMESTAMP);

    private static final Double DEFAULT_POWER = 1D;
    private static final Double UPDATED_POWER = 2D;

    private static final Double DEFAULT_CURRENT = 1D;
    private static final Double UPDATED_CURRENT = 2D;

    @Inject
    private PowerHistoryRepository powerHistoryRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restPowerHistoryMockMvc;

    private PowerHistory powerHistory;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PowerHistoryResource powerHistoryResource = new PowerHistoryResource();
        ReflectionTestUtils.setField(powerHistoryResource, "powerHistoryRepository", powerHistoryRepository);
        this.restPowerHistoryMockMvc = MockMvcBuilders.standaloneSetup(powerHistoryResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        powerHistory = new PowerHistory();
        powerHistory.setTimestamp(DEFAULT_TIMESTAMP);
        powerHistory.setPower(DEFAULT_POWER);
        powerHistory.setCurrent(DEFAULT_CURRENT);
    }

    @Test
    @Transactional
    public void createPowerHistory() throws Exception {
        int databaseSizeBeforeCreate = powerHistoryRepository.findAll().size();

        // Create the PowerHistory

        restPowerHistoryMockMvc.perform(post("/api/powerHistorys")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(powerHistory)))
                .andExpect(status().isCreated());

        // Validate the PowerHistory in the database
        List<PowerHistory> powerHistorys = powerHistoryRepository.findAll();
        assertThat(powerHistorys).hasSize(databaseSizeBeforeCreate + 1);
        PowerHistory testPowerHistory = powerHistorys.get(powerHistorys.size() - 1);
        assertThat(testPowerHistory.getTimestamp()).isEqualTo(DEFAULT_TIMESTAMP);
        assertThat(testPowerHistory.getPower()).isEqualTo(DEFAULT_POWER);
        assertThat(testPowerHistory.getCurrent()).isEqualTo(DEFAULT_CURRENT);
    }

    @Test
    @Transactional
    public void getAllPowerHistorys() throws Exception {
        // Initialize the database
        powerHistoryRepository.saveAndFlush(powerHistory);

        // Get all the powerHistorys
        restPowerHistoryMockMvc.perform(get("/api/powerHistorys?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(powerHistory.getId().intValue())))
                .andExpect(jsonPath("$.[*].timestamp").value(hasItem(DEFAULT_TIMESTAMP_STR)))
                .andExpect(jsonPath("$.[*].power").value(hasItem(DEFAULT_POWER.doubleValue())))
                .andExpect(jsonPath("$.[*].current").value(hasItem(DEFAULT_CURRENT.doubleValue())));
    }

    @Test
    @Transactional
    public void getPowerHistory() throws Exception {
        // Initialize the database
        powerHistoryRepository.saveAndFlush(powerHistory);

        // Get the powerHistory
        restPowerHistoryMockMvc.perform(get("/api/powerHistorys/{id}", powerHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(powerHistory.getId().intValue()))
            .andExpect(jsonPath("$.timestamp").value(DEFAULT_TIMESTAMP_STR))
            .andExpect(jsonPath("$.power").value(DEFAULT_POWER.doubleValue()))
            .andExpect(jsonPath("$.current").value(DEFAULT_CURRENT.doubleValue()));
    }

    @Test
    @Transactional
    public void getNonExistingPowerHistory() throws Exception {
        // Get the powerHistory
        restPowerHistoryMockMvc.perform(get("/api/powerHistorys/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePowerHistory() throws Exception {
        // Initialize the database
        powerHistoryRepository.saveAndFlush(powerHistory);

		int databaseSizeBeforeUpdate = powerHistoryRepository.findAll().size();

        // Update the powerHistory
        powerHistory.setTimestamp(UPDATED_TIMESTAMP);
        powerHistory.setPower(UPDATED_POWER);
        powerHistory.setCurrent(UPDATED_CURRENT);

        restPowerHistoryMockMvc.perform(put("/api/powerHistorys")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(powerHistory)))
                .andExpect(status().isOk());

        // Validate the PowerHistory in the database
        List<PowerHistory> powerHistorys = powerHistoryRepository.findAll();
        assertThat(powerHistorys).hasSize(databaseSizeBeforeUpdate);
        PowerHistory testPowerHistory = powerHistorys.get(powerHistorys.size() - 1);
        assertThat(testPowerHistory.getTimestamp()).isEqualTo(UPDATED_TIMESTAMP);
        assertThat(testPowerHistory.getPower()).isEqualTo(UPDATED_POWER);
        assertThat(testPowerHistory.getCurrent()).isEqualTo(UPDATED_CURRENT);
    }

    @Test
    @Transactional
    public void deletePowerHistory() throws Exception {
        // Initialize the database
        powerHistoryRepository.saveAndFlush(powerHistory);

		int databaseSizeBeforeDelete = powerHistoryRepository.findAll().size();

        // Get the powerHistory
        restPowerHistoryMockMvc.perform(delete("/api/powerHistorys/{id}", powerHistory.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<PowerHistory> powerHistorys = powerHistoryRepository.findAll();
        assertThat(powerHistorys).hasSize(databaseSizeBeforeDelete - 1);
    }
}
