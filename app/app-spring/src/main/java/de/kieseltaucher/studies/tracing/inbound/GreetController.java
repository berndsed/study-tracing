package de.kieseltaucher.studies.tracing.inbound;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import de.kieseltaucher.studies.tracing.app.GreetService;

@RestController
public class GreetController {

    @Autowired
    private GreetService greetService;

    @GetMapping("/hello")
    public String hello() {
        return greetService.getGreeting();
    }

}
