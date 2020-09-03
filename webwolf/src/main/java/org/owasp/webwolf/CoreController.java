package org.owasp.webwolf;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;

@RestController
public class CoreController {

    @RequestMapping(value = "/talk", method = RequestMethod.GET)
    public String talk(String talkee) {
        return "Hi";
    }
}
