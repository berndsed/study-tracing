package de.kieseltaucher.studies.tracing.app;

import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.ServiceUnavailableException;

@ApplicationScoped
public class TimeOfDayService {

    private final Logger logger = Logger.getLogger(getClass().getName());

    private int nextState;
    private final Availability availability = new Availability();

    public TimeOfDay getTimeOfDay() {
        throwIfUnavailable();
        int currentState = nextState;
        nextState = nextState + 1;
        return TimeOfDay.values()[currentState % TimeOfDay.values().length];
    }

    private void throwIfUnavailable() {
        if (!availability.isAvailable()) {
            logger.warning("Time-of-day-service is unavailable");
            throw new ServiceUnavailableException("The time-of-day-service is currently unavailable. Please come back later.");
        }
    }



}
