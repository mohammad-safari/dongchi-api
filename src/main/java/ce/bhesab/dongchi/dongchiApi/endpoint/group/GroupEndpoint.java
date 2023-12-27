package ce.bhesab.dongchi.dongchiApi.endpoint.group;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ce.bhesab.dongchi.dongchiApi.endpoint.group.dto.GroupCreateRequest;
import ce.bhesab.dongchi.dongchiApi.endpoint.group.dto.GroupCreateResponse;
import ce.bhesab.dongchi.dongchiApi.endpoint.group.dto.GroupRetrievalModel;
import ce.bhesab.dongchi.dongchiApi.endpoint.group.dto.MemberRetrievalModel;
import ce.bhesab.dongchi.dongchiApi.service.group.GroupService;
import ce.bhesab.dongchi.dongchiApi.service.group.model.GroupModel;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RestController
@RequestMapping("group")
@RequiredArgsConstructor
public class GroupEndpoint {

    private final GroupService groupService;

    @SneakyThrows
    @PostMapping
    public GroupCreateResponse createGroup(@Valid @RequestBody GroupCreateRequest groupCreateRequest,
            Authentication authentication) {
        var username = authentication.getName();
        groupService.createGroupIncludingRequestingUser(groupCreateRequest, username);
        return new GroupCreateResponse();
    }

    @SneakyThrows
    @GetMapping
    public List<GroupRetrievalModel> getGroups(Authentication authentication) {
        var username = authentication.getName();
        var groups = groupService.retrieveUserGroups(username);
        var test = convertEntityModelToRetrievalModel(username, groups);
        return test;
    }

    private List<GroupRetrievalModel> convertEntityModelToRetrievalModel(String username, List<GroupModel> groups) {
        return groups.stream().map(group -> GroupRetrievalModel.builder()
                .id(group.getId())
                .groupName(group.getGroupName())
                .description(group.getDescription())
                .groupImage(group.getGroupImage())
                .otherMembers(group.getMembers().stream()
                        // .filter(user -> !user.getUsername().equals(username))
                        .map(user -> MemberRetrievalModel.builder()
                                .username(user.getUsername())
                                .email(user.getEmail())
                                .phone(user.getPhone())
                                .build())
                        .toList())
                .build()).toList();
    }

}
