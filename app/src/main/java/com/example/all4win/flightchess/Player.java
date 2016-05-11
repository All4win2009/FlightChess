package com.example.all4win.flightchess;

/**
 * Created by All4win on 5/9/16.
 */
public class Player {
    private int id;
    private String user_name;
    private boolean isHost;
    private int img_id;
    private String host;
    Player(int i, String u, boolean is, int img){
        id = i;
        user_name = u;
        isHost = is;
        if (img == 1){
            img_id = R.drawable.player1;
        }
        else if (img == 2){
            img_id = R.drawable.player4;
        }
        else if (img == 3){
            img_id = R.drawable.player3;
        }
        else if (img == 4){
            img_id = R.drawable.player1;
        }

        if (is){
            host = "Host";
        }
        else {
            host = "";
        }
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

    public String getHost() {
        return host;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public boolean isHost() {
        return isHost;
    }

    public void setIsHost(boolean isHost) {
        this.isHost = isHost;
    }

    public int getImg_id() {
        return img_id;
    }

    public void setImg_id(int img_id) {
        this.img_id = img_id;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
