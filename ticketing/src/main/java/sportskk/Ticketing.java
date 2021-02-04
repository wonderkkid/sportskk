package sportskk;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;

@Entity
@Table(name="Ticketing_table")
public class Ticketing {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String teamCode;
    private Integer betCredit;
    private String ticketStatus;

    @PostPersist
    public void onPostPersist(){
        TicketPurchased ticketPurchased = new TicketPurchased();
        BeanUtils.copyProperties(this, ticketPurchased);
        ticketPurchased.publishAfterCommit();


    }

    @PreRemove
    public void onPreRemove(){
        TicketPurchaseCanceled ticketPurchaseCanceled = new TicketPurchaseCanceled();
        BeanUtils.copyProperties(this, ticketPurchaseCanceled);
        ticketPurchaseCanceled.publishAfterCommit();

        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

        sportskk.external.TicketTotalCancellation ticketTotalCancellation = new sportskk.external.TicketTotalCancellation();
        // mappings goes here
        TicketingApplication.applicationContext.getBean(sportskk.external.TicketTotalCancellationService.class)
            .ticketTotalCancel(ticketTotalCancellation);


    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getTeamCode() {
        return teamCode;
    }

    public void setTeamCode(String teamCode) {
        this.teamCode = teamCode;
    }
    public Integer getBetCredit() {
        return betCredit;
    }

    public void setBetCredit(Integer betCredit) {
        this.betCredit = betCredit;
    }
    public String getTicketStatus() {
        return ticketStatus;
    }

    public void setTicketStatus(String ticketStatus) {
        this.ticketStatus = ticketStatus;
    }




}
