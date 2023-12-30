package ce.bhesab.dongchi.dongchiApi.endpoint.group.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import ce.bhesab.dongchi.dongchiApi.service.event.model.EventType;
import lombok.Builder;

@Builder
public record GroupEventRetrievalModel(
        String creditorUsername,
        BigDecimal totalAmount,
        EventType type,
        List<String> participants,
        Map<String, String> shares) {

}
