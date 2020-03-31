package com.example.chitchat;

public class Messages {
    private String Message,Sender,type;
    public Messages()
    {

    }

    public Messages(String message, String sender, String type) {
        this.Message = message;
        this.Sender = sender;
        this.type = type;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getSender() {
        return Sender;
    }

    public void setSender(String sender) {
        Sender = sender;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
