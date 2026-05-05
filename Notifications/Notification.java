package Notifications;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Represents a system notification sent to a user.
 *
 * <p>Used for alerts (e.g., budget exceeded, goal completed) and reminders.
 *
 * @author DebugSquad
 * @version 1.0
 */
public class Notification implements Serializable {

    private int           notificationID;
    private int           userID;
    private String        type;
    private String        message;
    private boolean       isRead;
    private LocalDateTime timestamp;

    private static int idCounter = 1;

    /**
     * Constructs a new {@code Notification}.
     *
     * @param userID  the user to whom the notification belongs
     * @param type    the category/type of the notification
     * @param message the notification content
     */
    public Notification(int userID, String type, String message) {
        this.notificationID = idCounter++;
        this.userID         = userID;
        this.type           = type;
        this.message        = message;
        this.isRead         = false;
        this.timestamp      = LocalDateTime.now();
    }

    /** Marks this notification as read. */
    public void markAsRead() { this.isRead = true; }

    /** Dismisses this notification (also marks as read). */
    public void dismiss() { this.isRead = true; }

    /**
     * Updates the read status if the ID matches.
     *
     * @param notificationID the ID to match
     * @param isRead         the new read status
     */
    public void updateIsRead(int notificationID, boolean isRead) {
        if (this.notificationID == notificationID) this.isRead = isRead;
    }

    /** @return the notification ID */
    public int           getNotificationID()      { return notificationID; }
    /** @return the user ID */
    public int           getUserID()              { return userID; }
    /** @return the notification type */
    public String        getType()                { return type; }
    /** @return the notification message */
    public String        getMessage()             { return message; }
    /** @return {@code true} if the notification is read */
    public boolean       isRead()                 { return isRead; }
    /** @return the time the notification was created */
    public LocalDateTime getTimestamp()           { return timestamp; }

    /** @param id the new notification ID */
    public void setNotificationID(int id)         { this.notificationID = id; }
    /** @param counter the new static ID counter */
    public static void setIdCounter(int counter)  { idCounter = counter; }

    /**
     * Returns a formatted string representation of this notification.
     *
     * @return the formatted notification string
     */
    @Override
    public String toString() {
        return "[" + (isRead ? "READ" : "UNREAD") + "] "
                + type + " | " + message
                + " | " + timestamp.toLocalDate();
    }
}
