package ce.bhesab.dongchi.dongchiApi.endpoint.user;

import java.util.Base64;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ce.bhesab.dongchi.dongchiApi.endpoint.user.dto.AuthenticationPass;
import ce.bhesab.dongchi.dongchiApi.endpoint.user.dto.UserAuthenticationRequest;
import ce.bhesab.dongchi.dongchiApi.endpoint.user.dto.UserAuthenticationResponse;
import ce.bhesab.dongchi.dongchiApi.endpoint.user.dto.UserRegistrationRequest;
import ce.bhesab.dongchi.dongchiApi.endpoint.user.dto.UserRegistrationResponse;
import ce.bhesab.dongchi.dongchiApi.endpoint.user.exception.WrongEmailOrPasswordException;
import ce.bhesab.dongchi.dongchiApi.service.user.UserRepository;
import ce.bhesab.dongchi.dongchiApi.service.user.model.UserModel;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor

@RestController
@RequestMapping("user")
public class UserEndpoint {

    final UserRepository userRepository;
    final PasswordEncoder passwordEncoder;

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

}
