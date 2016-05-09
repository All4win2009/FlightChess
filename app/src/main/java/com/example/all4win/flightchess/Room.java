package com.example.all4win.flightchess;

/**
 * Created by All4win on 3/31/16.
 */
public class Room {
    private String roomName;
    private int currentNumber;
    private int roomNumber;
    private String state;
    private int host;
    public Room(int cur, int roomNum){
        host = -1;
        currentNumber = cur;
        roomNumber = roomNum;
        state = cur + "/4";
        roomName = "No." + roomNum;
    }


    public String getRoomName() {
        return roomName;
    }

    public int getCurrentNumber() {
        return currentNumber;
    }

    public void setCurrentNumber(int currentNumber) {
        this.currentNumber = currentNumber;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public String getState() {
        return state;
    }

    public int getHost() {
        return host;
    }

    public void setHost(int host) {
        this.host = host;
    }
}
