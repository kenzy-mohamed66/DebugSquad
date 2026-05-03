package Auth;

import java.io.Serializable;
import java.time.LocalDateTime;

public class User implements Serializable {

    private static int idCounter = 1;

    private int           userID;
    private String        fullName;
    private String        password;
    private String        email;
    private LocalDateTime createdAt;

    public User(String fullName, String email, String password) {
        this.userID    = idCounter++;
        this.fullName  = fullName;
        this.email     = email;
        this.password  = password;
        this.createdAt = LocalDateTime.now();
    }

    public boolean register() {
        if (fullName == null || fullName.isBlank()) return false;
        if (email == null || !email.contains("@")) return false;
        if (password == null || password.isBlank()) return false;
        return true;
    }

    public boolean login(String inputEmail, String inputPassword) {
        return this.email.equalsIgnoreCase(inputEmail)
                && this.password.equals(inputPassword);
    }

    public void logout() {
    }

    public int           getUserID()    { return userID; }
    public String        getFullName()  { return fullName; }
    public String        getEmail()     { return email; }
    public String        getPassword()  { return password; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setFullName(String n)   { this.fullName = n; }
    public void setEmail(String e)      { this.email = e; }
    public void setPassword(String p)   { this.password = p; }
}

