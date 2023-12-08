package ce.bhesab.dongchi.dongchiApi.endpoint.group;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ce.bhesab.dongchi.dongchiApi.endpoint.group.dto.GroupCreateRequest;
import ce.bhesab.dongchi.dongchiApi.endpoint.group.dto.GroupCreateResponse;
import ce.bhesab.dongchi.dongchiApi.endpoint.group.exception.UsernameNotFoundException;
import ce.bhesab.dongchi.dongchiApi.service.group.GroupRepository;
import ce.bhesab.dongchi.dongchiApi.service.group.model.GroupModel;
import ce.bhesab.dongchi.dongchiApi.service.user.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RestController
@RequestMapping("group")
@RequiredArgsConstructor
public class GroupEndpoint {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    @SneakyThrows
    @PostMapping
    public GroupCreateResponse createGroup(@Valid @RequestBody GroupCreateRequest groupCreateRequest,
            Authentication authentication) {
        var username = authentication.getName();
        var retrievedUser = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException());
        var group = GroupModel.builder().groupName(groupCreateRequest.groupName())
                .description(groupCreateRequest.description())
                .groupImage(groupCreateRequest.groupImage())
                .members(Set.of(retrievedUser)).build();
        groupRepository.save(group);
        return new GroupCreateResponse();
    }

    @SneakyThrows
    @GetMapping
    public List<GroupModel> getGroups(Authentication authentication) {
        var username = authentication.getName();
        var retrievedUser = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException());
        var groups = groupRepository.findByMembers(retrievedUser);
        var filteredGroups = groups.stream().map(g -> {
            var ownerOmitted = g.getMembers().stream().filter(u -> !u.getUsername().equals(authentication.getName()))
                    .collect(Collectors.toSet());
            g.setMembers(ownerOmitted);
            return g;
        }).toList();
        return filteredGroups;
    }

}
