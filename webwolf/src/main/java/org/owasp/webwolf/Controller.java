package org.owasp.webwolf;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;

@RestController
public class Controller {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home() {

        return "This is Home page";
    }

    @RequestMapping(value = "/greet", method = RequestMethod.GET)
    public String greet(String greetee) {
        return "Hello";
    }

    @RequestMapping(value = "/talk")
    public String talk(String talkee) {
        return "Hi";
    }

    @RequestMapping(value = "/about", method = RequestMethod.POST)
    public String about() {

        return "This is About page; POST request";
    }

    @RequestMapping(value = "/fresh", method = RequestMethod.GET)
    public String fresh() {

        return "This is Fresh page; GET/POST request";
    }

    @RequestMapping(value = "/time", method = RequestMethod.GET, params = { "info=time" })
    public String showTime() {

        var now = LocalTime.now();

        return String.format("%s", now.toString());
    }
}
