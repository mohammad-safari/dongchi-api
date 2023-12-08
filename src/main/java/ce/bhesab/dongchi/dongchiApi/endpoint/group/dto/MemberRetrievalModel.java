package ce.bhesab.dongchi.dongchiApi.endpoint.group.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record MemberRetrievalModel(
        Long id,
        @NotBlank String username,
        @Email @NotBlank String email,
        @Nullable @Size(max = 11, min = 11) @Digits(fraction = 0, integer = 11) String phone) {

}
