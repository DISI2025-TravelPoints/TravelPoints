package org.example.chatservice.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

record GreetingResponse(String name){}
record GreetingRequest(String name){}

@Controller
public class MessageController {
    @MessageMapping("/chatws")
    @SendTo("/topic/greetings")
    public String greeting(GreetingRequest message) throws Exception {
        return message.name();
    }
}
