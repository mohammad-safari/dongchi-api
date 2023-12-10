package ce.bhesab.dongchi.dongchiApi.endpoint.user.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UserProfileResponse(
        @NotBlank
        String username,
        @Email
        String email,
        @Nullable
        @Digits(fraction = 0, integer = 11)
        String phone,
        @Nullable
        UserBalanceResponse credit,
        @Nullable
        UserBalanceResponse debt) {
}
