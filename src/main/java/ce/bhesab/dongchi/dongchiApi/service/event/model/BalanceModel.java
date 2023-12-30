package ce.bhesab.dongchi.dongchiApi.service.event.model;

import java.math.BigDecimal;

import ce.bhesab.dongchi.dongchiApi.service.user.model.UserModel;
import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Builder
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
    private EventModel eventModel;

}