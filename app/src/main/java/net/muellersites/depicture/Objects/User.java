package net.muellersites.depicture.Objects;

public class User extends TempUser{

    private String Email;
    private String Password;
    private String Username;
    private String Token;


    public User(){
        this.Email = "";
        this.Password = "";
        this.Username = "";

        this.Token = "";
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }

    User(String email, String Password, String username, String token){
        this.Email = email;
        this.Password = Password;
        this.Username = username;
        this.Token = token;

    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getName() {
        return Username;
    }

    public void setName(String username) {
        Username = username;
    }
}
