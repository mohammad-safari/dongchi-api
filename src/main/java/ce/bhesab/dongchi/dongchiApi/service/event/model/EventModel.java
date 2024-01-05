package ce.bhesab.dongchi.dongchiApi.service.event.model;

import java.math.BigDecimal;
import java.util.Set;

import ce.bhesab.dongchi.dongchiApi.service.group.model.GroupModel;
import ce.bhesab.dongchi.dongchiApi.service.user.model.UserModel;
import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "EVENTS")
public class EventModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @NotNull
    @Builder.Default
    EventType type = EventType.Expense;
    BigDecimal totalAmount;
    @Nullable
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "eventModel")
    Set<BalanceModel> amountPerUser;

    @NotEmpty
    @ManyToMany
    Set<UserModel> participants;

    @NotNull
    @ManyToOne
    @EqualsAndHashCode.Exclude
    GroupModel groupScope;
}
