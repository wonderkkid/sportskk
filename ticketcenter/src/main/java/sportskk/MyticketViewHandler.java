package sportskk;

import sportskk.config.kafka.KafkaProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class MyticketViewHandler {


    @Autowired
    private MyticketRepository myticketRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whenTicketPurchased_then_CREATE_1 (@Payload TicketPurchased ticketPurchased) {
        try {
            if (ticketPurchased.isMe()) {
                // view 객체 생성
                Myticket myticket = new Myticket();
                // view 객체에 이벤트의 Value 를 set 함
                myticket.setTicketId(ticketPurchased.getId());
                myticket.setTeamCode(ticketPurchased.getTeamCode());
                myticket.setBetCredit(ticketPurchased.getBetCredit());
                // view 레파지 토리에 save
                myticketRepository.save(myticket);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whenTicketTotaled_then_UPDATE_1(@Payload TicketTotaled ticketTotaled) {
        try {
            if (ticketTotaled.isMe()) {
                // view 객체 조회
                List<Myticket> myticketList = myticketRepository.findByTicketId(ticketTotaled.getTicketId());
                for(Myticket myticket : myticketList){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    // view 레파지 토리에 save
                	myticket.setTicketStatus(ticketTotaled.getTicketStatus());
                	
                	myticketRepository.save(myticket);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenTicketTotaledCanceled_then_UPDATE_2(@Payload TicketTotaledCanceled ticketTotaledCanceled) {
        try {
            if (ticketTotaledCanceled.isMe()) {
                // view 객체 조회
            	List<Myticket> myticketList = myticketRepository.findByTicketId(ticketTotaledCanceled.getTicketId());
                for(Myticket myticket : myticketList){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    // view 레파지 토리에 save
                	myticket.setTicketStatus(ticketTotaledCanceled.getTicketStatus());
                	
                	myticketRepository.save(myticket);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenTicketPurchaseCanceled_then_DELETE_1(@Payload TicketPurchaseCanceled ticketPurchaseCanceled) {
        try {
            if (ticketPurchaseCanceled.isMe()) {
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}