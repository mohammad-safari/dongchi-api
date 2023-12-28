package ce.bhesab.dongchi.dongchiApi.service.group;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import ce.bhesab.dongchi.dongchiApi.endpoint.group.dto.GroupCreateRequest;
import ce.bhesab.dongchi.dongchiApi.service.group.exception.GroupNotFoundException;
import ce.bhesab.dongchi.dongchiApi.service.group.exception.InvalidGroupJoinCode;
import ce.bhesab.dongchi.dongchiApi.service.group.exception.UsernameNotFoundException;
import ce.bhesab.dongchi.dongchiApi.service.group.model.GroupJoinCode;
import ce.bhesab.dongchi.dongchiApi.service.group.model.GroupJoinCodeId;
import ce.bhesab.dongchi.dongchiApi.service.group.model.GroupModel;
import ce.bhesab.dongchi.dongchiApi.service.group.repository.GroupJoinCodeRepository;
import ce.bhesab.dongchi.dongchiApi.service.group.repository.GroupRepository;
import ce.bhesab.dongchi.dongchiApi.service.user.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Service
@RequiredArgsConstructor
public class GroupService {

    private static final String ADJECTIVE_KEY = "ADJECTIVES";
    private static final String NOUN_KEY = "NOUNS";
    private static final String ADJECTIVES_FILENAME = "adjective.txt";
    private static final String NOUNS_FILENAME = "noun.txt";

    private final GroupJoinCodeRepository groupJoinCodeRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    private Map<String, List<String>> dictionary; // ENUM MAP

    @SneakyThrows
    @PostConstruct
    public void buildDictionary() {
        dictionary = new HashMap<>();
        try (var reader = new BufferedReader(
                new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(NOUNS_FILENAME)))) {
            dictionary.put(NOUN_KEY, reader.lines().collect(Collectors.toList()));
        }
        try (var reader = new BufferedReader(
                new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(ADJECTIVES_FILENAME)))) {
            dictionary.put(ADJECTIVE_KEY, reader.lines().collect(Collectors.toList()));
        }

    }

    public void createGroupIncludingRequestingUser(GroupCreateRequest groupCreateRequest, String username)
            throws UsernameNotFoundException {
        var retrievedUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        var otherRetrievedUser = Optional.ofNullable(groupCreateRequest.otherMembers()).orElseGet(Set::of).stream()
                .map(otherUsername -> userRepository.findByUsername(otherUsername).orElse(null)).toList();
        if (otherRetrievedUser.contains(null)) {
            throw new UsernameNotFoundException();
        }
        var group = GroupModel.builder().groupName(groupCreateRequest.groupName())
                .description(groupCreateRequest.description())
                .groupImage(groupCreateRequest.groupImage())
                .members(Stream.concat(Set.of(retrievedUser).stream(), otherRetrievedUser.stream())
                        .collect(Collectors.toSet()))
                .build();
        groupRepository.save(group);
    }

    public List<GroupModel> retrieveUserGroups(String username) throws UsernameNotFoundException {
        var retrievedUser = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException());
        var groups = groupRepository.findByMembers(retrievedUser);
        return groups;
    }

    @Transactional
    public String generateJoinCodeAsUser(String username, Long groupId) throws GroupNotFoundException {
        // todo check user access
        var groupModel = groupRepository.findById(groupId).orElseThrow(GroupNotFoundException::new);
        var code = generateRandomStringByAdjectiveNoun(dictionary.get(NOUN_KEY), dictionary.get(ADJECTIVE_KEY));
        var joinCode = GroupJoinCode.builder().group(groupModel).code(code).build();
        var previousCodes = groupModel.getCodes();
        groupJoinCodeRepository.deleteAll(previousCodes);
        groupJoinCodeRepository.save(joinCode);
        groupModel.setCodes(Set.of(joinCode));
        return code;
    }

    private String generateRandomStringByAdjectiveNoun(List<String> nouns, List<String> adjectives) {
        var rand = new Random();
        var nounsCopy = new ArrayList<>(nouns);
        var adjectivesCopy = new ArrayList<>(adjectives);
        Collections.shuffle(nounsCopy);
        Collections.shuffle(adjectivesCopy);
        return new StringBuilder().append(adjectivesCopy.get(rand.nextInt(adjectivesCopy.size())))
                .append("-").append(nounsCopy.get(rand.nextInt(nounsCopy.size())))
                .append("-").append(rand.nextInt()).toString();
    }

    @Transactional
    public void addUserViaCode(String name, String code) throws InvalidGroupJoinCode, UsernameNotFoundException {
        var user = userRepository.findByUsername(name).orElseThrow(UsernameNotFoundException::new);
        var groupJoinCode = groupJoinCodeRepository.findByCode(code).orElseThrow(InvalidGroupJoinCode::new);
        var group = groupJoinCode.getGroup();
        group.setMembers(Stream.concat(group.getMembers().stream(), Set.of(user).stream()).collect(Collectors.toSet()));
    }

}
