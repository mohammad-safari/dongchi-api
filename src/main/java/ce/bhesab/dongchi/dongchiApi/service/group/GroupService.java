package ce.bhesab.dongchi.dongchiApi.service.group;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import ce.bhesab.dongchi.dongchiApi.endpoint.group.dto.GroupCreateRequest;
import ce.bhesab.dongchi.dongchiApi.service.group.exception.UsernameNotFoundException;
import ce.bhesab.dongchi.dongchiApi.service.group.model.GroupModel;
import ce.bhesab.dongchi.dongchiApi.service.user.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public void createGroupIncludingRequestingUser(GroupCreateRequest groupCreateRequest, String username)
            throws UsernameNotFoundException {
        var retrievedUser = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
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

}
