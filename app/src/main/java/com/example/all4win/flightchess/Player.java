package com.example.all4win.flightchess;

/**
 * Created by All4win on 5/9/16.
 */
public class Player {
    private int id;
    private String user_name;

    Player(int i, String u){
        id = i;
        u = user_name;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
}
