package ce.bhesab.dongchi.dongchiApi.endpoint.user;

import ce.bhesab.dongchi.dongchiApi.endpoint.user.dto.*;
import ce.bhesab.dongchi.dongchiApi.endpoint.user.exception.WrongEmailOrPasswordException;
import ce.bhesab.dongchi.dongchiApi.service.user.UserRepository;
import ce.bhesab.dongchi.dongchiApi.service.user.model.UserModel;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserEndpoint {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("registration")
    public UserRegistrationResponse register(@Valid @RequestBody UserRegistrationRequest userRegistrationModel) {
        var userModel = UserModel.builder()
                .username(userRegistrationModel.username())
                .password(passwordEncoder.encode(userRegistrationModel.password()))
                .email(userRegistrationModel.email())
                .phone(userRegistrationModel.phone()).build();
        userRepository.save(userModel);
        return new UserRegistrationResponse();
    }

    @SneakyThrows
    @PostMapping("authentication")
    public UserAuthenticationResponse postMethodName(
            @Valid @RequestBody UserAuthenticationRequest userAuthenticationRequest,
            HttpServletResponse response) {
        if (userAuthenticationRequest.pass() == AuthenticationPass.USERNAME) {
            var retrievedUser = userRepository.findByUsername(userAuthenticationRequest.username()).orElse(null);
            if (retrievedUser != null
                    && passwordEncoder.matches(userAuthenticationRequest.password(), retrievedUser.getPassword())) {
                var authorizationHeader = "Basic " +
                        Base64.getEncoder().encodeToString((retrievedUser.getUsername() + ":"
                                + userAuthenticationRequest.password()).getBytes());
                response.setHeader("Authorization", authorizationHeader);
                return new UserAuthenticationResponse(authorizationHeader);
            }
        }

        throw new WrongEmailOrPasswordException();
    }

    @SneakyThrows
    @GetMapping("profile")
    public UserProfileResponse getUserProfile(Authentication authentication) {
        var retrievedUser = userRepository.findByUsername(authentication.getName()).orElseThrow();
        return UserProfileResponse.builder()
                .username(retrievedUser.getUsername())
                .email(retrievedUser.getEmail())
                .phone(retrievedUser.getPhone())
                .credit(UserBalanceResponse.builder()
                        .currency("IRR")
                        .amount(300000L).build())
                .debt(UserBalanceResponse.builder()
                        .currency("IRR")
                        .amount(100000L).build())
                .build();
    }
}
