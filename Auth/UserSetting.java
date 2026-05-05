package Auth;

import java.io.Serializable;

/**
 * Stores notification and display settings for a user account.
 *
 * <p>Each flag controls whether a particular type of alert or reminder
 * is enabled. The appearance field controls the UI theme.
 * Implements {@link Serializable} to allow persistence via {@code DataManager}.
 *
 * @author DebugSquad
 * @version 1.0
 */
public class UserSetting implements Serializable {

    private boolean notificationsOn    = true;
    private boolean budgetAlertsOn     = true;
    private boolean goalRemindersOn    = true;
    private String  appearance         = "default";

    /**
     * Persists the current settings.
     */
    public void save() {
        System.out.println("[Settings] Settings saved.");
    }

    /**
     * Toggles the budget alerts flag on or off and prints the new state.
     */
    public void toggleBudgetAlerts() {
        budgetAlertsOn = !budgetAlertsOn;
        System.out.println("[Settings] Budget alerts: " + (budgetAlertsOn ? "ON" : "OFF"));
    }

    /**
     * Toggles the goal reminders flag on or off and prints the new state.
     */
    public void toggleGoalReminders() {
        goalRemindersOn = !goalRemindersOn;
        System.out.println("[Settings] Goal reminders: " + (goalRemindersOn ? "ON" : "OFF"));
    }

    /**
     * Applies the current settings and notifies the user.
     */
    public void update() {
        System.out.println("[System] Settings updated successfully.");
    }

    /** @return {@code true} if general notifications are enabled */
    public boolean isNotificationsOn()   { return notificationsOn; }
    /** @return {@code true} if budget alerts are enabled */
    public boolean isBudgetAlertsOn()    { return budgetAlertsOn; }
    /** @return {@code true} if goal reminders are enabled */
    public boolean isGoalRemindersOn()   { return goalRemindersOn; }
    /** @return the current UI appearance theme name */
    public String  getAppearance()       { return appearance; }
    /** @param v {@code true} to enable general notifications */
    public void setNotificationsOn(boolean v)  { this.notificationsOn = v; }
    /** @param v {@code true} to enable budget alerts */
    public void setBudgetAlertsOn(boolean v)   { this.budgetAlertsOn = v; }
    /** @param v {@code true} to enable goal reminders */
    public void setGoalRemindersOn(boolean v)  { this.goalRemindersOn = v; }
    /** @param v the new appearance theme name */
    public void setAppearance(String v)        { this.appearance = v; }
}