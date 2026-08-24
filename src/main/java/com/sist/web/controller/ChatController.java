package com.sist.web.controller;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.sist.web.vo.ChatMessage;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatController {
	private final SimpMessagingTemplate template;
	
	@MessageMapping("/chat/public")
	@SendTo("/topic/chat")
	public ChatMessage publicChat(ChatMessage msg, Principal p) {
		msg.setSender(p.getName());
		
		return msg;
	}
	
	@GetMapping("/chat")
	public String chat_chat(Model model) {
		model.addAttribute("main_html","chat/chat");
		
		return "chat";
	}
}
