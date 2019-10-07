package de.kieseltaucher.studies.tracing.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.EnumSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TimeOfDayServiceTest {

    private TimeOfDayService timeOfDayEndpoint;

    @BeforeEach
    void setUp() {
        timeOfDayEndpoint = new TimeOfDayService();
    }

    @Test
    void rollsState() {
        final TimeOfDay state1 = timeOfDayEndpoint.getTimeOfDay();
        final TimeOfDay state2 = timeOfDayEndpoint.getTimeOfDay();
        assertNotEquals(state1, state2);
    }

    @Test
    void rollsAllStates() {
        final Set<TimeOfDay> actualStates = EnumSet.noneOf(TimeOfDay.class);
        for (int iter = 0; iter < TimeOfDay.values().length; ++iter) {
            actualStates.add(timeOfDayEndpoint.getTimeOfDay());
        }
        assertEquals(EnumSet.allOf(TimeOfDay.class), actualStates);
    }

}
