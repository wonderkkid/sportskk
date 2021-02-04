package sportskk;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MyticketRepository extends CrudRepository<Myticket, Long> {

    List<Myticket> findByTicketId(Long ticketId);
   // List<> findByTicketId(Long ticketId);

}