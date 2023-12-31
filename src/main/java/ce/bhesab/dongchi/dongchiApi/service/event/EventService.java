package ce.bhesab.dongchi.dongchiApi.service.event;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import ce.bhesab.dongchi.dongchiApi.service.event.model.BalanceModel;
import ce.bhesab.dongchi.dongchiApi.service.event.model.EventModel;
import ce.bhesab.dongchi.dongchiApi.service.event.model.EventType;
import ce.bhesab.dongchi.dongchiApi.service.event.repository.BalanceRepository;
import ce.bhesab.dongchi.dongchiApi.service.event.repository.EventRepository;
import ce.bhesab.dongchi.dongchiApi.service.group.exception.GroupNotFoundException;
import ce.bhesab.dongchi.dongchiApi.service.group.exception.UsernameNotFoundException;
import ce.bhesab.dongchi.dongchiApi.service.group.repository.GroupRepository;
import ce.bhesab.dongchi.dongchiApi.service.user.UserRepository;
import ce.bhesab.dongchi.dongchiApi.service.user.model.UserModel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventService {
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final EventRepository eventRepository;
    private final BalanceRepository balanceRepository;

    public void addGroupEvent(Long groupId, String creditorUsername, BigDecimal totalAmount,
            EventType type, Map<String, BigDecimal> participantsUserNameShareMap)
            throws GroupNotFoundException, UsernameNotFoundException {
        var relatedGroup = groupRepository.findById(groupId).orElseThrow(GroupNotFoundException::new);
        var creditor = userRepository.findByUsername(creditorUsername).orElseThrow(UsernameNotFoundException::new);
        var participants = participantsUserNameShareMap.keySet().stream().map(userRepository::findByUsername)
                .map(Optional::get).toList();
        if (participants.contains(null)) {
            throw new UsernameNotFoundException();
        }
        var eventModel = EventModel.builder().groupScope(relatedGroup).totalAmount(totalAmount)
                .participants(Set.copyOf(participants)).type(type).build();
        var balanceModelList = addAllGroupBalances(participantsUserNameShareMap, creditor, participants, eventModel);
        eventModel.setAmountPerUser(Set.copyOf(balanceModelList));
        eventRepository.save(eventModel);
    }

    private List<BalanceModel> addAllGroupBalances(Map<String, BigDecimal> participantsUserNameShareMap,
            UserModel creditor, List<UserModel> participants, EventModel eventModel) {
        var balanceModelList = participantsUserNameShareMap.entrySet().stream()
                .filter(entry -> !entry.getKey().equals(creditor.getUsername()))
                .map(entry -> {
                    var debtorUsername = entry.getKey();
                    var debtorUser = participants.stream().filter(p -> p.getUsername().equals(debtorUsername)).findAny()
                            .orElse(null);
                    return BalanceModel.builder().creditor(creditor).debtor(debtorUser).eventModel(eventModel)
                            .amount(participantsUserNameShareMap.get(debtorUsername)).build();
                }).toList();
        balanceRepository.saveAll(balanceModelList);
        return balanceModelList;
    }

    public List<EventModel> getAllGroupEvents(Long groupId) throws GroupNotFoundException {
        var relatedGroup = groupRepository.findById(groupId).orElseThrow(GroupNotFoundException::new);
        return eventRepository.findByGroupScope(relatedGroup);
    }

}
