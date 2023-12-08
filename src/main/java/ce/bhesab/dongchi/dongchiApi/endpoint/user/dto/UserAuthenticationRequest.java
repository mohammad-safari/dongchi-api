package ce.bhesab.dongchi.dongchiApi.endpoint.user.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserAuthenticationRequest(
        @Nullable String username,
        @NotBlank String password,
        @Nullable @Email String email,
        @Nullable @Size(max = 11, min = 11) @Digits(fraction = 0, integer = 11) String phone,
        AuthenticationPass pass) {
            
    public UserAuthenticationRequest {
        pass = (pass != null) ? pass : AuthenticationPass.USERNAME;
    }

}
