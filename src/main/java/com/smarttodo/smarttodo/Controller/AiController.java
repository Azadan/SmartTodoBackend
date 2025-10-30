package com.smarttodo.smarttodo.Controller;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/ai")
public class AiController {

    private final ChatClient chatClient;
    private final List<Message> conversation;
    final String systemMessageString = """
                Du är en AI-assistent som hjälper användare att skapa detaljerade och engagerande beskrivningar
                för sina vanor i en vanespårningsapp. 
                Din uppgift är att ta emot en kort beskrivning från användaren om en vana de vill 
                följa och generera en inspirerande, tydlig och motiverande text 
                (max 500 ord, inklusive mellanslag, radbrytningar och skiljetecken)
                som uppmuntrar till konsekvent uppföljning och genomförande av vanan. Om någon ställer en annan fråga än
                att skapa en vana beskrivning, svara med "Jag kan bara hjälpa till med att skapa vanebeskrivningar. 
                ". Förutsatt att frågan inte är kopplad till den vanan som ska beskrivas.
                """;

    public AiController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
        this.conversation = new ArrayList<>();
        final SystemMessage systemMessage = new SystemMessage(systemMessageString);
        this.conversation.add(systemMessage);
    }

    @GetMapping("/suggestDescription")
    public String suggestDescription(@RequestParam (name = "message", defaultValue = "Provide a detailed description for a habit item") String message){
        final Message userMessage = new UserMessage(message);
        this.conversation.add(userMessage);
        String modelResponse = this.chatClient.prompt()
                .messages(this.conversation)
                .call()
                .content();

        final Message assistantMessage = new AssistantMessage(modelResponse);
        this.conversation.add(assistantMessage);
        return modelResponse;
    }

}
