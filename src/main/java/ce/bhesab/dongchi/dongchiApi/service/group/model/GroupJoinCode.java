package ce.bhesab.dongchi.dongchiApi.service.group.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@IdClass(GroupJoinCodeId.class)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "GROUP_JOIN_CODES")
public class GroupJoinCode {
    @Id
    @ManyToOne
    GroupModel group;
    @Id
    @Column(unique = true)
    String code;
}