package sportskk;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;

@Entity
@Table(name="TicketTotal_table")
public class TicketTotal {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Long ticketId;
    private String status;

    @PostPersist
    public void onPostPersist(){
        TicketTotaled ticketTotaled = new TicketTotaled();
        BeanUtils.copyProperties(this, ticketTotaled);
        ticketTotaled.publishAfterCommit();


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
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }




}
