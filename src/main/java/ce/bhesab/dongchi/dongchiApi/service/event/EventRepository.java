package ce.bhesab.dongchi.dongchiApi.service.event;

import ce.bhesab.dongchi.dongchiApi.service.event.model.EventModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface EventRepository extends JpaRepository<EventModel, Long> {

}
