package notifications;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Notification implements Serializable {
    // ─── Fields (match class diagram) ───────────────────────────────────────
    private int notificationID; 
    private int userID;
    private String type;
    private String message;
    private boolean isRead;
    private LocalDateTime timestamp;

    // ─── Static counter for ID generation ───────────────────────────────────
    private static int idCounter = 1;

    // ─── Constructor ─────────────────────────────────────────────────────────
    public Notification(int userID, String type, String message) {
        this.notificationID = idCounter++;
        this.userID         = userID;
        this.type           = type;
        this.message        = message;
        this.isRead         = false;
        this.timestamp      = LocalDateTime.now();
    }

    // ─── Methods from sequence diagrams ──────────────────────────────────────

    /**
     * US#11 — NotificationUI calls this when user taps a notification.
     * Marks notification as read.
     */
    public void markAsRead() {
        this.isRead = true;
    }

    /**
     * US#11 — NotificationUI calls this when user dismisses a notification.
     */
    public void dismiss() {
        this.isRead = true;
        // Additional dismiss logic can go here (e.g. remove from list)
    }

    /**
     * US#11 (seq diagram) — updateIsRead called internally.
     */
    public void updateIsRead(int notificationID, boolean isRead) {
        if (this.notificationID == notificationID) {
            this.isRead = isRead;
        }
    }

    // ─── Getters ─────────────────────────────────────────────────────────────
    public int getNotificationID()      { return notificationID; }
    public int getUserID()              { return userID; }
    public String getType()             { return type; }
    public String getMessage()          { return message; }
    public boolean isRead()             { return isRead; }
    public LocalDateTime getTimestamp() { return timestamp; }

    // ─── Setters ─────────────────────────────────────────────────────────────
    public void setNotificationID(int notificationID) { this.notificationID = notificationID; }
    public static void setIdCounter(int counter)      { idCounter = counter; }

    @Override
    public String toString() {
        return "[" + (isRead ? "READ" : "UNREAD") + "] "
                + type + " | " + message
                + " | " + timestamp.toLocalDate();
    }
}
