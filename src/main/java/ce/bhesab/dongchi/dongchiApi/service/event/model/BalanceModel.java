package ce.bhesab.dongchi.dongchiApi.service.event.model;

import java.math.BigDecimal;

import ce.bhesab.dongchi.dongchiApi.service.user.model.UserModel;
import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "BALANCES")
public class BalanceModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Nonnull
    @ManyToOne
    private UserModel creditor;

    @Nonnull
    @ManyToOne
    private UserModel debtor;

    @Nonnull
    private BigDecimal amount;

    @Nonnull
    @ManyToOne
    @EqualsAndHashCode.Exclude
    private EventModel eventModel;

}