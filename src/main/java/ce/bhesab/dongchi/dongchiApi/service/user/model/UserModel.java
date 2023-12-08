package ce.bhesab.dongchi.dongchiApi.service.user.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
@Table(name = "USERS")
public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @NotBlank(message = "{username.notempty}")
    @Column(unique = true)
    String username;
    @NotBlank
    String password;
    @Email
    @NotBlank(message = "{email.notempty}")
    @Column(unique = true)
    String email;
    @Nullable
    @Size(max = 11, min = 11)
    @Digits(fraction = 0, integer = 11)
    @Column(unique = true)
    String phone;
}
