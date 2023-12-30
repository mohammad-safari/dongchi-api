package ce.bhesab.dongchi.dongchiApi.endpoint.group.dto;

import java.math.BigDecimal;
import java.util.Map;

import ce.bhesab.dongchi.dongchiApi.service.event.model.EventType;

public record EventPostRequest(
        String creditorUsername, BigDecimal totalAmount, EventType getEventType,
        Map<String, BigDecimal> participantsUserNameShareMap) {

}
