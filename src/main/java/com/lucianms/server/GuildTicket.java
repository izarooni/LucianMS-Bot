package com.lucianms.server;

public class GuildTicket {

    private final int ticketID;
    private final String userID;
    private final String destinationMessageID;
    private final String creationMessageID;
    private boolean completed;

    public GuildTicket(int ticketID, String userID, String destinationMessageID, String creationMessageID) {
        this.ticketID = ticketID;
        this.userID = userID;
        this.destinationMessageID = destinationMessageID;
        this.creationMessageID = creationMessageID;
    }

    public int getTicketID() {
        return ticketID;
    }

    public String getUserID() {
        return userID;
    }

    public String getDestinationMessageID() {
        return destinationMessageID;
    }

    public String getCreationMessageID() {
        return creationMessageID;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
