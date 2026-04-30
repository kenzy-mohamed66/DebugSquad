package UI;

import models.User;
import models.Notification;
import data.DataManager;

import java.util.List;
import java.util.Scanner;

/**
 * NotificationUI — «boundary» class for US#11 Notification Center. 
 *
 * Sequence diagram interactions implemented:
 *   displayNotifications() → Notification.getNotifications()
 *   create(type, message)   → new Notification(...)  [when notifications exist]
 *   showEmpty()             → [when no notifications]
 *   navigateToSource()
 *   markAsRead(notificationID) → Notification.markAsRead()
 *   updateIsRead(...)          → Notification.updateIsRead()
 *   dismiss()                  → Notification.dismiss()
 */
public class NotificationUI {

    private Scanner scanner;
    private User currentUser;

    public NotificationUI(Scanner scanner, User currentUser) {
        this.scanner     = scanner;
        this.currentUser = currentUser;
    }

    // ─── Main entry point ─────────────────────────────────────────────────────
    public void displayNotifications() {
        System.out.println("\n╔══════════════════════════════╗");
        System.out.println("║      NOTIFICATION CENTER     ║");
        System.out.println("╚══════════════════════════════╝");

        List<Notification> notifList =
                DataManager.getNotificationsByUser(currentUser.getUserID());

        // US#11 seq: alt [notifications exist] vs [no notifications]
        if (notifList.isEmpty()) {
            showEmpty();
            return;
        }

        // Count unread
        long unread = notifList.stream().filter(n -> !n.isRead()).count();
        System.out.println("You have " + unread + " unread notification(s).\n");

        // Display all notifications, most recent first
        for (int i = notifList.size() - 1; i >= 0; i--) {
            Notification n = notifList.get(i);
            System.out.println((notifList.size() - i) + ". " + n);
        }

        showNotificationMenu(notifList);
    }

    // ─── Sub-menu ─────────────────────────────────────────────────────────────
    private void showNotificationMenu(List<Notification> notifList) {
        System.out.println("\n1. Mark a notification as read");
        System.out.println("2. Dismiss a notification");
        System.out.println("3. Navigate to source");
        System.out.println("4. Back");
        System.out.print("Choice: ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1" -> markAsReadPrompt(notifList);
            case "2" -> dismissPrompt(notifList);
            case "3" -> navigateToSource(notifList);
            case "4" -> { /* return */ }
            default  -> System.out.println("Invalid choice.");
        }
    }

    // ─── US#11: markAsRead ────────────────────────────────────────────────────
    private void markAsReadPrompt(List<Notification> notifList) {
        System.out.print("Enter notification number to mark as read: ");
        try {
            int index = Integer.parseInt(scanner.nextLine().trim());
            // list was printed in reverse, so reverse the index
            int realIndex = notifList.size() - index;
            if (realIndex < 0 || realIndex >= notifList.size()) {
                System.out.println("Invalid number.");
                return;
            }
            Notification n = notifList.get(realIndex);
            markAsRead(n.getNotificationID()); // calls seq diagram method
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    /**
     * US#11 seq: markAsRead(notificationID) → Notification.updateIsRead()
     */
    public void markAsRead(int notificationID) {
        List<Notification> all = DataManager.getNotificationsByUser(currentUser.getUserID());
        for (Notification n : all) {
            if (n.getNotificationID() == notificationID) {
                n.updateIsRead(notificationID, true); // exact seq diagram call
                DataManager.updateNotification(n);
                System.out.println("Notification marked as read.");
                return;
            }
        }
        System.out.println("Notification not found.");
    }

    // ─── US#11: dismiss ───────────────────────────────────────────────────────
    private void dismissPrompt(List<Notification> notifList) {
        System.out.print("Enter notification number to dismiss: ");
        try {
            int index    = Integer.parseInt(scanner.nextLine().trim());
            int realIndex = notifList.size() - index;
            if (realIndex < 0 || realIndex >= notifList.size()) {
                System.out.println("Invalid number.");
                return;
            }
            Notification n = notifList.get(realIndex);
            n.dismiss(); // exact seq diagram call
            DataManager.updateNotification(n);
            System.out.println("Notification dismissed.");
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    // ─── US#11: navigateToSource ──────────────────────────────────────────────
    /**
     * US#11 seq: navigateToSource() — navigates user to the relevant screen.
     * In terminal, we print what triggered the notification.
     */
    public void navigateToSource(List<Notification> notifList) {
        System.out.print("Enter notification number to navigate to its source: ");
        try {
            int index     = Integer.parseInt(scanner.nextLine().trim());
            int realIndex = notifList.size() - index;
            if (realIndex < 0 || realIndex >= notifList.size()) {
                System.out.println("Invalid number.");
                return;
            }
            Notification n = notifList.get(realIndex);
            System.out.println("\n[Source] " + n.getType() + ": " + n.getMessage());
            markAsRead(n.getNotificationID());
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    // ─── US#11: showEmpty ─────────────────────────────────────────────────────
    /**
     * US#11 seq alt [no notifications]: showEmpty()
     */
    public void showEmpty() {
        System.out.println("No new notifications.");
    }
}
