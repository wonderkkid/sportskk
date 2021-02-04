package sportskk;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;

@Entity
@Table(name="TicketTotalCancellation_table")
public class TicketTotalCancellation {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Long ticketId;
    private String ticketStatus;

    @PostPersist
    public void onPostPersist(){
        TicketTotaledCanceled ticketTotaledCanceled = new TicketTotaledCanceled();
        BeanUtils.copyProperties(this, ticketTotaledCanceled);
        ticketTotaledCanceled.publishAfterCommit();


    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }
    public String getTicketStatus() {
        return ticketStatus;
    }

    public void setTicketStatus(String ticketStatus) {
        this.ticketStatus = ticketStatus;
    }




}
