package ce.bhesab.dongchi.dongchiApi.endpoint.user.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.springframework.format.annotation.NumberFormat;

@Builder
public record UserBalanceResponse(
        @NotBlank
        String targetName,
        @Nullable
        String currency,
        @Nullable
        @NumberFormat
        Long amount) {
}
