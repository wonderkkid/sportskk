package sportskk;

public class TicketPurchased extends AbstractEvent {

    private Long id;
    private String teamCode;
    private Integer betCredit;

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
}