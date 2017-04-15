package net.muellersites.depicture.Objects;


import java.io.Serializable;

public class TempUser implements Serializable{

    private Integer Uid;
    private String Username;
    private String Token;
    private String Firebase_ID;


    public TempUser(){
        this.Uid = 0;
        this.Username = "";

        this.Token = "";
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }

    TempUser(Integer uid, String email, String Password, String username, String token){
        this.Uid = uid;
        this.Username = username;
        this.Token = token;

    }

    public Integer getId() {
        return Uid;
    }

    public void setId(Integer uid) {
        Uid = uid;
    }

    public String getName() {
        return Username;
    }

    public void setName(String username) {
        Username = username;
    }

    public String getFirebase_ID() {
        return Firebase_ID;
    }

    public void setFirebase_ID(String firebase_ID) {
        Firebase_ID = firebase_ID;
    }
}
