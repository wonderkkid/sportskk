package sportskk;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="Myticket_table")
public class Myticket {

        @Id
        @GeneratedValue(strategy=GenerationType.AUTO)
        private Long id;
        private Long ticketId;
        private String teamCode;
        private Integer betCredit;
        private String ticketStatus;


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
