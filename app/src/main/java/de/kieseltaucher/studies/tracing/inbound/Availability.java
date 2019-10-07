package de.kieseltaucher.studies.tracing.inbound;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Availability implements Serializable {

    private int state;

    boolean isAvailable() {
        ++state;
        return state % 4 != 0;
    }

}
