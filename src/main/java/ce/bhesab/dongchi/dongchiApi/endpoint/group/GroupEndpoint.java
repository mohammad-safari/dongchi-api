package ce.bhesab.dongchi.dongchiApi.endpoint.group;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ce.bhesab.dongchi.dongchiApi.endpoint.group.dto.EventPostRequest;
import ce.bhesab.dongchi.dongchiApi.endpoint.group.dto.EventPostResponse;
import ce.bhesab.dongchi.dongchiApi.endpoint.group.dto.GroupCreateRequest;
import ce.bhesab.dongchi.dongchiApi.endpoint.group.dto.GroupCreateResponse;
import ce.bhesab.dongchi.dongchiApi.endpoint.group.dto.GroupEventRetrievalModel;
import ce.bhesab.dongchi.dongchiApi.endpoint.group.dto.GroupRetrievalModel;
import ce.bhesab.dongchi.dongchiApi.endpoint.group.dto.JoinGroupResponse;
import ce.bhesab.dongchi.dongchiApi.endpoint.group.dto.JoinGroupResuest;
import ce.bhesab.dongchi.dongchiApi.endpoint.group.dto.MemberRetrievalModel;
import ce.bhesab.dongchi.dongchiApi.service.event.EventService;
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
    private final EventService eventService;

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

    @SneakyThrows
    @GetMapping("{groupId}/join-code")
    public String getJoinCode(Authentication authentication, @PathVariable Long groupId) {
        return groupService.generateJoinCodeAsUser(authentication.getName(), groupId);
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

    @SneakyThrows
    @PostMapping("join-code")
    public JoinGroupResponse joinGroupViaJoinCode(Authentication authentication,
            @Valid @RequestBody JoinGroupResuest joinRequest) {
        groupService.addUserViaCode(authentication.getName(), joinRequest.code());
        return new JoinGroupResponse();
    }

    @SneakyThrows
    @GetMapping("{groupId}/events")
    public List<GroupEventRetrievalModel> retrieveGroupEvents(Authentication authentication,
            @PathVariable Long groupId) {
        // todo pagination and access check
        var events = eventService.getAllGroupEvents(groupId);
        return events.stream().map(e -> GroupEventRetrievalModel.builder()
                .creditorUsername(e.getAmountPerUser().iterator().next().getCreditor().getUsername())
                .totalAmount(e.getTotalAmount())
                .type(e.getType()).participants(e.getParticipants().stream().map(p -> p.getUsername()).toList())
                .shares(e.getAmountPerUser().stream()
                        .map(b -> Map.entry(b.getDebtor().getUsername(), b.getAmount().toString()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
                .build()).toList();
    }

    @SneakyThrows
    @PostMapping("{groupId}/event")
    public EventPostResponse postGroupEvent(Authentication authentication,
            @PathVariable Long groupId, @RequestBody EventPostRequest request) {
        eventService.addGroupEvent(groupId, request.creditorUsername(), request.totalAmount(),
                request.getEventType(), request.participantsUserNameShareMap());
        return new EventPostResponse();
    }

}
