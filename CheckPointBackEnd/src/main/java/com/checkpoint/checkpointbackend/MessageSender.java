package com.checkpoint.checkpointbackend;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageSender {
     @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @PostMapping("/send")
    public String sendMessage(@RequestBody MessageFormat messageFormat,
                              Principal principal) {

        // Use authenticated user as sender if available
        String sender = (principal != null) ? principal.getName() : messageFormat.getSender();

        if (sender == null || sender.isEmpty()) {
            return "Error: sender is not defined.";
        }

        for (String recipient : messageFormat.getRecipients()) {
            MessageFormat msgToSend = new MessageFormat();
            msgToSend.setSender(sender);
            msgToSend.setRecipients(null);
            msgToSend.setContent(messageFormat.getContent());

            messagingTemplate.convertAndSendToUser(
                recipient,
                "/queue/messages",
                msgToSend
            );
            System.out.println("Sent message from " + sender + " to " + recipient);
        }

        return "Message sent to " + messageFormat.getRecipients();
    }
}
