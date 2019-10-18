package de.kieseltaucher.studies.tracing.inbound;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import de.kieseltaucher.studies.tracing.tracing.app.ServiceUnavailableException;
import de.kieseltaucher.studies.tracing.tracing.app.TimeOfDay;
import de.kieseltaucher.studies.tracing.tracing.app.TimeOfDayService;

@RestController
public class TimeOfDayController {

    @Autowired
    private TimeOfDayService timeOfDayService;

    @GetMapping("/time-of-day")
    public TimeOfDay doGet() {
        try {
            return timeOfDayService.getTimeOfDay();
        } catch (ServiceUnavailableException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage(), e);
        }
    }
}
