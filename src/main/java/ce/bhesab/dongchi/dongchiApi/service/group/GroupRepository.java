package ce.bhesab.dongchi.dongchiApi.service.group;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface GroupRepository extends JpaRepository<String,String> {
    
}
