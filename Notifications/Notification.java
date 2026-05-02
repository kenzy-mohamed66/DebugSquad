package Notifications;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Notification implements Serializable {

    private int           notificationID;
    private int           userID;
    private String        type;
    private String        message;
    private boolean       isRead;
    private LocalDateTime timestamp;

    private static int idCounter = 1;

    public Notification(int userID, String type, String message) {
        this.notificationID = idCounter++;
        this.userID         = userID;
        this.type           = type;
        this.message        = message;
        this.isRead         = false;
        this.timestamp      = LocalDateTime.now();
    }

    public void markAsRead() { this.isRead = true; }

    public void dismiss() { this.isRead = true; }

    public void updateIsRead(int notificationID, boolean isRead) {
        if (this.notificationID == notificationID) this.isRead = isRead;
    }

    public int           getNotificationID()      { return notificationID; }
    public int           getUserID()              { return userID; }
    public String        getType()                { return type; }
    public String        getMessage()             { return message; }
    public boolean       isRead()                 { return isRead; }
    public LocalDateTime getTimestamp()           { return timestamp; }

    public void setNotificationID(int id)         { this.notificationID = id; }
    public static void setIdCounter(int counter)  { idCounter = counter; }

    @Override
    public String toString() {
        return "[" + (isRead ? "READ" : "UNREAD") + "] "
                + type + " | " + message
                + " | " + timestamp.toLocalDate();
    }
}
