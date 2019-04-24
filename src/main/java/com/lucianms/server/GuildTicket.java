package com.lucianms.server;

public class GuildTicket {

    private final int ticketID;
    private final long userID;
    private final long destinationMessageID;
    private final long creationMessageID;
    private boolean completed;

    public GuildTicket(int ticketID, long userID, long destinationMessageID, long creationMessageID) {
        this.ticketID = ticketID;
        this.userID = userID;
        this.destinationMessageID = destinationMessageID;
        this.creationMessageID = creationMessageID;
    }

    public int getTicketID() {
        return ticketID;
    }

    public long getUserID() {
        return userID;
    }

    public long getDestinationMessageID() {
        return destinationMessageID;
    }

    public long getCreationMessageID() {
        return creationMessageID;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
