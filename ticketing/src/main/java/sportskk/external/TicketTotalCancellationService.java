
package sportskk.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@FeignClient(name="tickettotal", url="http://tickettotal:8080")
public interface TicketTotalCancellationService {

    @RequestMapping(method= RequestMethod.POST, path="/ticketTotalCancellations")
    public void ticketTotalCancel(@RequestBody TicketTotalCancellation ticketTotalCancellation);

}