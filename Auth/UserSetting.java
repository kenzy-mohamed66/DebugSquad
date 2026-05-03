package Auth;

import java.io.Serializable;

public class UserSetting implements Serializable {

    private boolean notificationsOn    = true;
    private boolean budgetAlertsOn     = true;
    private boolean goalRemindersOn    = true;
    private String  appearance         = "default";

    public void save() {
        System.out.println("[Settings] Settings saved.");
    }

    public void toggleBudgetAlerts() {
        budgetAlertsOn = !budgetAlertsOn;
        System.out.println("[Settings] Budget alerts: " + (budgetAlertsOn ? "ON" : "OFF"));
    }

    public void toggleGoalReminders() {
        goalRemindersOn = !goalRemindersOn;
        System.out.println("[Settings] Goal reminders: " + (goalRemindersOn ? "ON" : "OFF"));
    }

    public void update() {
        System.out.println("[System] Settings updated successfully.");
    }

    public boolean isNotificationsOn()   { return notificationsOn; }
    public boolean isBudgetAlertsOn()    { return budgetAlertsOn; }
    public boolean isGoalRemindersOn()   { return goalRemindersOn; }
    public String  getAppearance()       { return appearance; }
    public void setNotificationsOn(boolean v)  { this.notificationsOn = v; }
    public void setBudgetAlertsOn(boolean v)   { this.budgetAlertsOn = v; }
    public void setGoalRemindersOn(boolean v)  { this.goalRemindersOn = v; }
    public void setAppearance(String v)        { this.appearance = v; }
}