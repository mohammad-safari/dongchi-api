package ce.bhesab.dongchi.dongchiApi.service.group.model;

import java.io.Serializable;
import java.util.Set;

import org.apache.commons.lang3.builder.HashCodeExclude;
import org.apache.commons.lang3.builder.ToStringExclude;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ce.bhesab.dongchi.dongchiApi.service.user.model.UserModel;
import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode.Exclude;

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

    @OneToMany(mappedBy = "group") 
    @EqualsAndHashCode.Exclude
    Set<GroupJoinCode> codes;
}
