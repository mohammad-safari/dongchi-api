package ce.bhesab.dongchi.dongchiApi.service.user;

import java.util.Base64;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ce.bhesab.dongchi.dongchiApi.endpoint.user.dto.UserBalanceResponse;
import ce.bhesab.dongchi.dongchiApi.endpoint.user.dto.UserProfileResponse;
import ce.bhesab.dongchi.dongchiApi.endpoint.user.dto.UserRegistrationRequest;
import ce.bhesab.dongchi.dongchiApi.endpoint.user.dto.UserRegistrationWithoutUsernameRequest;
import ce.bhesab.dongchi.dongchiApi.service.group.GroupService;
import ce.bhesab.dongchi.dongchiApi.service.user.exception.WrongEmailOrPasswordException;
import ce.bhesab.dongchi.dongchiApi.service.user.model.UserModel;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final GroupService groupService;
    private final PasswordEncoder passwordEncoder;

    public void registerUser(UserRegistrationRequest userRegistrationModel) {
        var userModel = UserModel.builder()
                .username(userRegistrationModel.username())
                .password(passwordEncoder.encode(userRegistrationModel.password()))
                .email(userRegistrationModel.email())
                .phone(userRegistrationModel.phone()).build();
        userRepository.save(userModel);
    }

    public String registerUserWithoutUsername(@Valid UserRegistrationWithoutUsernameRequest userRegistrationModel) {
        var dictionary = groupService.getDictionary();
        var randomUsername = groupService.generateRandomStringByAdjectiveNoun(
                dictionary.get(GroupService.NOUN_KEY), dictionary.get(GroupService.ADJECTIVE_KEY));
        var userModel = UserModel.builder()
                .username(randomUsername)
                .password(passwordEncoder.encode(userRegistrationModel.password()))
                .email(userRegistrationModel.email())
                .phone(userRegistrationModel.phone()).build();
        userRepository.save(userModel);
        return randomUsername;

    }

    public String authenticateUsingPassword(String username, String password) throws WrongEmailOrPasswordException {
        var retrievedUser = userRepository.findByUsername(username).orElse(null);
        if (retrievedUser == null || !passwordEncoder.matches(password, retrievedUser.getPassword())) {
            throw new WrongEmailOrPasswordException();
        }
        var authorizationHeader = "Basic "
                + Base64.getEncoder().encodeToString((retrievedUser.getUsername() + ":" + password).getBytes());
        return authorizationHeader;
    }

    public UserProfileResponse retrieveUserProfile(Authentication authentication) {
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
