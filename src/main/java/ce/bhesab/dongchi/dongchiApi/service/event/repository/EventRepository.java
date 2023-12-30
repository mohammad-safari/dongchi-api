package ce.bhesab.dongchi.dongchiApi.service.event.repository;

import ce.bhesab.dongchi.dongchiApi.service.event.model.EventModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.List;
import ce.bhesab.dongchi.dongchiApi.service.group.model.GroupModel;

@RepositoryRestResource
public interface EventRepository extends JpaRepository<EventModel, Long> {
    List<EventModel> findByGroupScope(GroupModel groupScope);
}
