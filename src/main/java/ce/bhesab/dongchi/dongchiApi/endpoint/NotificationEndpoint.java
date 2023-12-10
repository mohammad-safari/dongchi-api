package ce.bhesab.dongchi.dongchiApi.endpoint;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/sse")
@Tag(name = "SSE API")
public class NotificationEndpoint {

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ApiResponse(description = "SSE stream")
    public SseEmitter streamSseMvc() {
        // Your SSE logic here
        SseEmitter emitter = new SseEmitter();
        // Add logic to send events to the emitter
        return emitter;
    }
}

