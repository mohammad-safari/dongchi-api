package ce.bhesab.dongchi.dongchiApi.service.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import ce.bhesab.dongchi.dongchiApi.service.event.model.BalanceModel;

@RepositoryRestResource
public interface BalanceRepository extends JpaRepository<BalanceModel, Long> {

}
