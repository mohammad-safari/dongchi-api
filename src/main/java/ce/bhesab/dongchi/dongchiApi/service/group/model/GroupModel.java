package ce.bhesab.dongchi.dongchiApi.service.group.model;

import java.util.Objects;
import java.util.Set;

import ce.bhesab.dongchi.dongchiApi.service.user.model.UserModel;
import io.micrometer.common.lang.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "GROUPS")
public class GroupModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @NotBlank
    String groupName;
    @Nullable
    @Size(max = 500)
    String description;
    /* estimated size of max 500KB */
    @Nullable
    @Size(max = 670000)
    String groupImage;

    @NotEmpty
    @ManyToMany
    Set<UserModel> members;
}
