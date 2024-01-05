package ce.bhesab.dongchi.dongchiApi.service.event;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class EventService {
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final EventRepository eventRepository;
    private final BalanceRepository balanceRepository;

    @CacheEvict(value = "debts", key = "#group.id")
    @Transactional
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

    @Transactional
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
        return balanceRepository.saveAll(balanceModelList);
    }

    public List<EventModel> getAllGroupEvents(Long groupId) throws GroupNotFoundException {
        var relatedGroup = groupRepository.findById(groupId).orElseThrow(GroupNotFoundException::new);
        return eventRepository.findByGroupScope(relatedGroup);
    }

    @Cacheable(value = "optimizedBalance", key = "#group.id")
    public Map<String, Map<String, BigDecimal>> optimizeGroupBalance(Long groupId) throws GroupNotFoundException {
        var netBalanceMap = calculateNetBalance(groupId);
        var optimizedMap = new HashMap<String, Map<String, BigDecimal>>();

        Comparator<Entry<String, BigDecimal>> descendingComparator = (bal1, bal2) -> bal2.getValue().abs()
                .compareTo(bal1.getValue().abs());
        var descendingSortedNameList = netBalanceMap.entrySet().stream().sorted(descendingComparator)
                .map(Map.Entry::getKey)
                .toList();

        for (var creditor : descendingSortedNameList) {
            optimizedMap.put(creditor, new HashMap<>());

            for (var debtor : descendingSortedNameList) {
                var creditorBalance = netBalanceMap.get(creditor);
                var debtorBalance = netBalanceMap.get(debtor);

                if (!creditor.equals(debtor) && creditorBalance.compareTo(BigDecimal.ZERO) > 0
                        && debtorBalance.compareTo(BigDecimal.ZERO) < 0) {
                    var minTransaction = creditorBalance.min(debtorBalance.abs());
                    optimizedMap.get(creditor).put(debtor, minTransaction);
                    // update net balance
                    netBalanceMap.get(creditor).subtract(minTransaction);
                    netBalanceMap.get(debtor).add(minTransaction);
                }
            }
        }
        return optimizedMap;
    }

    @Cacheable(value = "netBalance", key = "#group.id")
    public Map<String, BigDecimal> calculateNetBalance(Long groupId) throws GroupNotFoundException {
        var netBalanceMap = new HashMap<String, BigDecimal>();
        var events = getAllGroupEvents(groupId);
        for (EventModel event : events) {
            var participants = event.getParticipants();
            var totalAmount = event.getTotalAmount();
            var balances = event.getAmountPerUser();

            // Calculate individual contribution for each participant
            var individualContribution = totalAmount.divide(BigDecimal.valueOf(participants.size()), 2,
                    RoundingMode.HALF_UP);

            // Update net balances based on contributions
            for (var participant : participants) {
                var contribution = balances.stream()
                        .filter(balance -> balance.getCreditor().equals(participant))
                        .map(BalanceModel::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                contribution = contribution.subtract(balances.stream()
                        .filter(balance -> balance.getDebtor().equals(participant))
                        .map(BalanceModel::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add));

                netBalanceMap.put(participant.getUsername(),
                        netBalanceMap.getOrDefault(participant, BigDecimal.ZERO).add(contribution));
            }
        }

        return netBalanceMap;
    }

}
