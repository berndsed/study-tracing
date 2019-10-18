package de.kieseltaucher.studies.tracing.tracing.app;

import java.io.Serializable;

class Availability implements Serializable {

    private int state;

    boolean isAvailable() {
        ++state;
        return state % 4 != 0;
    }

}
