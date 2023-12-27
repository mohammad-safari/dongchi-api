package ce.bhesab.dongchi.dongchiApi.service.group.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ce.bhesab.dongchi.dongchiApi.service.group.model.GroupJoinCode;

@Repository
public interface GroupJoinCodeRepository extends JpaRepository<GroupJoinCode, String> {
}
