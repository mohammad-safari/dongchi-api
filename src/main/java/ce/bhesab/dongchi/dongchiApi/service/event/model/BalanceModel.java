package ce.bhesab.dongchi.dongchiApi.service.event.model;

import ce.bhesab.dongchi.dongchiApi.service.user.model.UserModel;
import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
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
    private Long amount;

    @Nonnull
    @ManyToOne
    private EventModel eventModel;

}