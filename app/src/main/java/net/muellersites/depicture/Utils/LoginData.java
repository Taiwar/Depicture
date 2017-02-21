package net.muellersites.depicture.Utils;

public class LoginData {
    private String Name;
    private String Password;

    public LoginData() {
        Name = "";
        Password = "";
    }

    public LoginData(String name, String password) {
        Name = name;
        Password = password;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
