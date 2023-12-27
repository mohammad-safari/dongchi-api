package ce.bhesab.dongchi.dongchiApi.endpoint.user;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ce.bhesab.dongchi.dongchiApi.endpoint.user.dto.AuthenticationPass;
import ce.bhesab.dongchi.dongchiApi.endpoint.user.dto.UserAuthenticationRequest;
import ce.bhesab.dongchi.dongchiApi.endpoint.user.dto.UserAuthenticationResponse;
import ce.bhesab.dongchi.dongchiApi.endpoint.user.dto.UserProfileResponse;
import ce.bhesab.dongchi.dongchiApi.endpoint.user.dto.UserRegistrationRequest;
import ce.bhesab.dongchi.dongchiApi.endpoint.user.dto.UserRegistrationResponse;
import ce.bhesab.dongchi.dongchiApi.endpoint.user.exception.IllegalAuthenticationPassException;
import ce.bhesab.dongchi.dongchiApi.service.user.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserEndpoint {

    private final UserService userService;

    @PostMapping("registration")
    public UserRegistrationResponse register(@Valid @RequestBody UserRegistrationRequest userRegistrationModel) {
        userService.registerUser(userRegistrationModel);
        return new UserRegistrationResponse();
    }

    @SneakyThrows
    @PostMapping("authentication")
    public UserAuthenticationResponse authenticate(
            @Valid @RequestBody UserAuthenticationRequest userAuthenticationRequest,
            HttpServletResponse response) {
        if (userAuthenticationRequest.pass() == AuthenticationPass.USERNAME) {
            var authorizationHeader = userService.authenticateUsingPassword(
                    userAuthenticationRequest.username(), userAuthenticationRequest.password());
            response.setHeader("Authorization", authorizationHeader);
            return new UserAuthenticationResponse(authorizationHeader);
        }
        throw new IllegalAuthenticationPassException();
    }

    @SneakyThrows
    @GetMapping("profile")
    public UserProfileResponse getUserProfile(Authentication authentication) {
        return userService.retrieveUserProfile(authentication);
    }

}
