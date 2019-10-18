package de.kieseltaucher.studies.tracing.tracing.app;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AvailabilityTest {

    private Availability availability;

    @BeforeEach
    void setUp() {
        availability = new Availability();
    }

    @Test
    void initiallyAvailable() {
        assertTrue(availability.isAvailable());
    }

    @Test
    void isUnavailableSometimes() {
        boolean alwaysAvailable = true;
        for (int iter = 0; iter < 10; ++iter) {
            alwaysAvailable = alwaysAvailable && availability.isAvailable();
        }
        assertFalse(alwaysAvailable);
    }

}
