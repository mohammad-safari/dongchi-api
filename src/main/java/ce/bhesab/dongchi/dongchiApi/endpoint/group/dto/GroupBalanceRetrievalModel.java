package ce.bhesab.dongchi.dongchiApi.endpoint.group.dto;

import java.math.BigDecimal;
import java.util.Map;

public record GroupBalanceRetrievalModel(Map<String, Map<String, BigDecimal>> netBalanceMap) {

}
