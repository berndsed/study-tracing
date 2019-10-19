package de.kieseltaucher.studies.tracing.outbound;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import de.kieseltaucher.studies.tracing.app.TimeOfDay;

@Component
class TimeOfDayClientImpl implements TimeOfDayClient {

    @Autowired
    private RestTemplate template;

    @Override
    public TimeOfDay getTimeOfDay() {
        return template.getForObject("http://localhost:8080/time-of-day", TimeOfDay.class);
    }
}
