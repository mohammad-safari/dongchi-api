package ce.bhesab.dongchi.dongchiApi.endpoint;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("event")
public class EventEndpoint {
    @GetMapping
    public void getEvents(Authentication authentication) {
    }

    @PostMapping
    public void addEvents(Authentication authentication) {
    }
}
