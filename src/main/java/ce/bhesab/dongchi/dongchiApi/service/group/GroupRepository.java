package ce.bhesab.dongchi.dongchiApi.service.group;

import java.util.List;

import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ce.bhesab.dongchi.dongchiApi.service.group.model.GroupModel;
import ce.bhesab.dongchi.dongchiApi.service.user.model.UserModel;

@Repository
public interface GroupRepository extends JpaRepository<GroupModel, Long> {
     List<UserModel> findByMembers(User user);

}
