package com.checkpoint.checkpointbackend;

import java.util.List;

public class MessageFormat {
    private String sender;
    private List<String> recipients;
    private String content;

    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    public List<String> getRecipients() { return recipients; }
    public void setRecipients(List<String> recipients) { this.recipients = recipients; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
