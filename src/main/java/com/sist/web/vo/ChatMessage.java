package com.sist.web.vo;

import lombok.Data;

@Data
public class ChatMessage {
	private String sender, receiver, message;
}
