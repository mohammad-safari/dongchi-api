package ce.bhesab.dongchi.dongchiApi.service.group.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class GroupJoinCodeId implements Serializable {
  private GroupModel group;
  private String code;
}