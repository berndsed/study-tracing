package de.kieseltaucher.studies.tracing.domain;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import de.kieseltaucher.studies.tracing.outbound.TimeOfDayClient;
import de.kieseltaucher.studies.tracing.tracing.TraceLogger;

@Dependent
public class GreetService {

    @Inject
    private TimeOfDayClient timeOfDayClient;

    @Inject
    private TraceLogger traceLogger;

    public String getGreeting() {
        TimeOfDay timeOfDay = getTimeOfDay();
        return chooseResponse(timeOfDay);
    }

    private TimeOfDay getTimeOfDay() {
        traceLogger.log("Calling time-of-day-service");
        TimeOfDay timeOfDay = timeOfDayClient.getTimeOfDay();
        traceLogger.log(String.format("The time of day according to time-of-day-service is %s", timeOfDay));
        return timeOfDay;
    }

    private String chooseResponse(TimeOfDay timeOfDay) {
        if (timeOfDay == null) {
            return "Hi";
        }
        switch (timeOfDay) {
            case MORNING:
                return "Good morning";
            case AFTERNOON:
                return "Good afternoon";
            case EVENING:
                return "Good night";
            default:
                throw new IllegalStateException("My goodness, it's the middle of the night");
        }
    }
}
