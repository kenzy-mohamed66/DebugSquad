package UI;

import Auth.User;
import Notifications.Notification;
import data.DataManager;

import java.util.List;
import java.util.Scanner;

public class NotificationUI {

    private final Scanner scanner;
    private final User    currentUser;

    public NotificationUI(Scanner scanner, User currentUser) {
        this.scanner     = scanner;
        this.currentUser = currentUser;
    }

    public void displayNotifications() {
        System.out.println("\n╔══════════════════════════════╗");
        System.out.println("║      NOTIFICATION CENTER     ║");
        System.out.println("╚══════════════════════════════╝");

        List<Notification> notifList = DataManager.getNotificationsByUser(currentUser.getUserID());

        if (notifList.isEmpty()) { showEmpty(); return; }

        long unread = notifList.stream().filter(n -> !n.isRead()).count();
        System.out.println("You have " + unread + " unread notification(s).\n");

        for (int i = notifList.size() - 1; i >= 0; i--) {
            Notification n = notifList.get(i);
            System.out.println((notifList.size() - i) + ". " + n);
        }

        showNotificationMenu(notifList);
    }

    private void showNotificationMenu(List<Notification> notifList) {
        System.out.println("\n1. Mark a notification as read");
        System.out.println("2. Dismiss a notification");
        System.out.println("3. Back");
        System.out.print("Choice: ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1" -> markAsReadPrompt(notifList);
            case "2" -> dismissPrompt(notifList);
            case "3" -> { /* return */ }
            default  -> System.out.println("Invalid choice.");
        }
    }

    private void markAsReadPrompt(List<Notification> notifList) {
        System.out.print("Enter notification number: ");
        try {
            int index = Integer.parseInt(scanner.nextLine().trim());
            int realIndex = notifList.size() - index;
            if (realIndex < 0 || realIndex >= notifList.size())
                { System.out.println("Invalid number."); return; }
            Notification n = notifList.get(realIndex);
            markAsRead(n.getNotificationID());
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    public void markAsRead(int notificationID) {
        List<Notification> all = DataManager.getNotificationsByUser(currentUser.getUserID());
        for (Notification n : all) {
            if (n.getNotificationID() == notificationID) {
                n.updateIsRead(notificationID, true);
                DataManager.updateNotification(n);
                System.out.println("Notification marked as read.");
                return;
            }
        }
        System.out.println("Notification not found.");
    }

    private void dismissPrompt(List<Notification> notifList) {
        System.out.print("Enter notification number to dismiss: ");
        try {
            int index    = Integer.parseInt(scanner.nextLine().trim());
            int realIndex = notifList.size() - index;
            if (realIndex < 0 || realIndex >= notifList.size())
                { System.out.println("Invalid number."); return; }
            Notification n = notifList.get(realIndex);
            n.dismiss();
            DataManager.updateNotification(n);
            System.out.println("Notification dismissed.");
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    public void navigateToSource(List<Notification> notifList) {
        System.out.print("Enter notification number: ");
        try {
            int index    = Integer.parseInt(scanner.nextLine().trim());
            int realIndex = notifList.size() - index;
            if (realIndex < 0 || realIndex >= notifList.size())
                { System.out.println("Invalid number."); return; }
            Notification n = notifList.get(realIndex);
            System.out.println("\n[Source] " + n.getType() + ": " + n.getMessage());
            markAsRead(n.getNotificationID());
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    public void showEmpty() { System.out.println("No new notifications."); }
}
