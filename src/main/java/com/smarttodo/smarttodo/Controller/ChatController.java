package com.smarttodo.smarttodo.Controller;

import com.smarttodo.smarttodo.Request.ChatMessageRequest;
import com.smarttodo.smarttodo.Response.ChatMessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {

    // Mall för att skicka STOMP-meddelanden till destinationer och användare
    private  final SimpMessagingTemplate template;


    /*
     * Tar emot meddelanden från användaren och skickar dom till admin-kanalent
     * Topic: /topic/support/admin
     * Behörighet: User och Premium
     */
    @MessageMapping("/support/message") // Mottagningspunkt för meddelanden
    @SendTo("/topic/support/admin") // Broadcast till alla admins som lyssnar
    @PreAuthorize("hasAnyRole('USER','PREMIUM')") // Endast authentiserade användare med rollerna USER eller PREMIUM
    public ChatMessageResponse userToAdmin(ChatMessageRequest req, Authentication auth) {
        return new ChatMessageResponse(auth.getName(), req.text());
    }

    /*
     * En administratör skickaR ett meddelande till en specifik användare
     * Destination: /user/{email}/queue/support
     * Behörighet: Admin
     */
    @MessageMapping("/support/to/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public void adminToUser(@DestinationVariable String email,
                            ChatMessageRequest req,
                            Authentication auth) {
        template.convertAndSendToUser(
                email,
                "/queue/support",
                new ChatMessageResponse(auth.getName(), req.text())
        );
    }
}
