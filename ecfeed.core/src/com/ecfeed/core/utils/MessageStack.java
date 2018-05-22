package com.ecfeed.core.utils;

import java.util.ArrayList;

public class MessageStack {
	
	private ArrayList<String> fMessages;
	
	public MessageStack() {
		fMessages = new ArrayList<String>();
	}
	
	public void addMessage(String message) {
		fMessages.add(0, message);
	}
	
	public String getLongMessage() {
		
		StringBuilder sb = new StringBuilder();
		
		for (String message : fMessages) {
			sb.append(message);
			sb.append(" ");
		}
		
		return sb.toString();
	}

}
