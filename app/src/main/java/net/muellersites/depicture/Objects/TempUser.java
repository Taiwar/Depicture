package net.muellersites.depicture.Objects;


import java.io.Serializable;

public class TempUser implements Serializable{

    private Integer Uid;
    private String Username;
    private String Token;
    private String InstanceID;


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

    public String getInstanceID() {
        return InstanceID;
    }

    public void setInstanceID(String instanceID) {
        InstanceID = instanceID;
    }
}
