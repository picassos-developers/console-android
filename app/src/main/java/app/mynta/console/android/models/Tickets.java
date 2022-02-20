package app.mynta.console.android.models;

public class Tickets {
    private int id;
    private int ticketId;
    private String ticketSubject;
    private String ticketMessage;
    private String ticketOwnerEmail;
    private String ticketType;
    private String ticketDate;
    private String ticketExpireDate;
    private String ticketStatus;

    public Tickets(int id, int ticketId, String ticketSubject, String ticketMessage, String ticketOwnerEmail, String ticketType, String ticketDate, String ticketExpireDate, String ticketStatus) {
        this.id = id;
        this.ticketId = ticketId;
        this.ticketSubject = ticketSubject;
        this.ticketMessage = ticketMessage;
        this.ticketOwnerEmail = ticketOwnerEmail;
        this.ticketType = ticketType;
        this.ticketDate = ticketDate;
        this.ticketExpireDate = ticketExpireDate;
        this.ticketStatus = ticketStatus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public String getTicketSubject() {
        return ticketSubject;
    }

    public void setTicketSubject(String ticketSubject) {
        this.ticketSubject = ticketSubject;
    }

    public String getTicketMessage() {
        return ticketMessage;
    }

    public void setTicketMessage(String ticketMessage) {
        this.ticketMessage = ticketMessage;
    }

    public String getTicketOwnerEmail() {
        return ticketOwnerEmail;
    }

    public void setTicketOwnerEmail(String ticketOwnerEmail) {
        this.ticketOwnerEmail = ticketOwnerEmail;
    }

    public String getTicketType() {
        return ticketType;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public String getTicketDate() {
        return ticketDate;
    }

    public void setTicketDate(String ticketDate) {
        this.ticketDate = ticketDate;
    }

    public String getTicketExpireDate() {
        return ticketExpireDate;
    }

    public void setTicketExpireDate(String ticketExpireDate) {
        this.ticketExpireDate = ticketExpireDate;
    }

    public String getTicketStatus() {
        return ticketStatus;
    }

    public void setTicketStatus(String ticketStatus) {
        this.ticketStatus = ticketStatus;
    }
}
