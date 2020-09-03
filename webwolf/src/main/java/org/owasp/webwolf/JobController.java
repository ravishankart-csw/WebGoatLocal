package org.owasp.webwolf;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;

@RestController
public class JobController {

    @RequestMapping(value = "/jobs")
    public String job(String jobname) {
        return "List of Jobs";
    }
}
