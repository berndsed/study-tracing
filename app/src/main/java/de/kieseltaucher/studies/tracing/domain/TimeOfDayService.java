package de.kieseltaucher.studies.tracing.domain;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TimeOfDayService {

    private int nextState;

    public TimeOfDay getTimeOfDay() {
        int currentState = nextState;
        nextState = nextState + 1;
        return TimeOfDay.values()[currentState % TimeOfDay.values().length];
    }

}
