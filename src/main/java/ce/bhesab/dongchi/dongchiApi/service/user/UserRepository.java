package ce.bhesab.dongchi.dongchiApi.service.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ce.bhesab.dongchi.dongchiApi.service.user.model.UserModel;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Long> {
     Optional<UserModel> findByUsername(String username);

     Optional<UserModel> findByEmail(String email);

     Optional<UserModel> findByPhone(String phone);
}
