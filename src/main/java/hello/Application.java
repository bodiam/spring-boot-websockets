package hello;

/**
 * @author Erik Pragt
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.messaging.simp.broker.BrokerAvailabilityEvent;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

@SpringBootApplication
@EnableScheduling
public class Application implements ApplicationListener<BrokerAvailabilityEvent> {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Autowired
    private MessageSendingOperations<String> messagingTemplate;

    private AtomicBoolean brokerAvailable = new AtomicBoolean();


    @Override
    public void onApplicationEvent(BrokerAvailabilityEvent event) {
        this.brokerAvailable.set(event.isBrokerAvailable());
    }

    @Scheduled(fixedDelay = 2000)
    public void sendQuotes() {
        System.out.println("sendQuotes");

        if (this.brokerAvailable.get()) {
            System.out.println("convertAndSend");

            this.messagingTemplate.convertAndSend("/topic/greetings", new Greeting("Hello : " + new Date().toString()));
        }
    }
}