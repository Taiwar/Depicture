package net.muellersites.kritzel.Objects;

public class User {

    private Integer Uid;
    private String Email;
    private String Password;
    private String Username;
    private String Token;


    public User(){
        this.Uid = 0;
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

    User(Integer uid, String email, String Password, String username, String token){
        this.Uid = uid;
        this.Email = email;
        this.Password = Password;
        this.Username = username;
        this.Token = token;

    }

    public Integer getId() {
        return Uid;
    }

    public void setId(Integer uid) {
        Uid = uid;
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
