package my.edu.utar.assignment;

// Model class to store password data
public class PasswordModel {

    private String site;
    private String username;
    private String password;

    // Constructor
    public PasswordModel(String site, String username, String password) {
        this.site = site;
        this.username = username;
        this.password = password;
    }

    // Getter methods
    public String getSite() { return site; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
}