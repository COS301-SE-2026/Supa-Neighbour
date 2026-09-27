package com.app.api.config;
 
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
 
/**
 * Turns on Spring's {@code @Scheduled} support so
 * {@code EndorsementClusterService#runNightlyForAllZones()} actually fires.
 *
 * <p>If the main {@code @SpringBootApplication} class (or another
 * {@code @Configuration} class not seen while building this feature) already
 * carries {@code @EnableScheduling}, this is a harmless duplicate — Spring
 * tolerates it — but it's worth removing one of the two to keep it obvious
 * where scheduling is turned on.</p>
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {
}
