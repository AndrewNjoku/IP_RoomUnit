package com.tunstall.grandstream;

public class OutgoingMessage {
	
	private long messageId;
	private String message;
	private String resident;
	private String appVersion;
    private String callbackNumber;
    private String deviceType;
	
	public OutgoingMessage(String message, long messageId, String resident, String appVersion, String callbackNumber, String deviceType) {
		this.messageId = messageId;
		this.message = message;
		this.resident = resident;
		this.appVersion = appVersion;
        this.callbackNumber = callbackNumber;
        this.deviceType = deviceType;
	}
	
	public long getMessageId() {
		return messageId;
	}
	
	public String getData() {
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" ?><message protocol=\"1\">");
		sb.append("<messageId>");
		sb.append(String.valueOf(messageId));
		sb.append("</messageId>");
		sb.append("<resident>");
		sb.append(resident);
		sb.append("</resident>");
		sb.append("<command>");
		sb.append(message);
		sb.append("</command>");
		sb.append("<appVersion>");
        sb.append(appVersion);
        sb.append("</appVersion>");
       sb.append("<callbackNumber>");
        sb.append(callbackNumber);
        sb.append("</callbackNumber>");
        sb.append("<deviceType>");
        sb.append(deviceType);
        sb.append("</deviceType>");
		sb.append("</message>");
		return sb.toString();
	}

}
