# DebugSquad - Masroofy Personal Budgeting System

## Folder and File Structure

DebugSquad/
├── Main.java               # Entry point of the application
├── README.md               # Project documentation
├── Auth/                   # Authentication and User Domain
│   ├── Admin.java
│   ├── User.java
│   ├── UserProfile.java
│   └── UserSetting.java
├── Budget/                 # Budgeting Logic and Constraints
│   ├── Budget.java
│   └── BudgetAlert.java
├── Goals/                  # Financial Goal Tracking
│   └── FinancialGoal.java
├── Notifications/          # System Alerts and Notifications
│   └── Notification.java
├── Reports/                # Exporting and Reporting Logic
│   └── Report.java
├── Transaction/            # Financial Transactions
│   ├── Category.java
│   └── Transaction.java
├── UI/                     # Presentation Layer (CLI Menus)
│   ├── BudgetUI.java
│   ├── DashboardUI.java
│   ├── ExportUI.java
│   ├── GoalUI.java
│   ├── LoginUI.java
│   ├── NotificationUI.java
│   ├── ProfileUI.java
│   ├── ReportUI.java
│   ├── SignUpUI.java
│   └── TransactionUI.java
└── data/                   # Data Persistence Layer
    └── DataManager.java
    
---------------------------------------------------------------------------------------

## Architecture

This project strictly follows a **3-Layer Architecture**:
1.Presentation Layer: The `UI` package containing all interactive console menus and user workflows.
2.Domain/Business Layer: Packages like `Auth`, `Budget`, `Goals`, `Transaction`, `Reports`, and `Notifications` housing the core business logic and entities.
3.Data Access Layer: The `data` package (`DataManager.java`) managing the persistence of objects natively via Java Serialization.