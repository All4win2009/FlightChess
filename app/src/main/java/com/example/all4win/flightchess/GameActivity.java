package com.example.all4win.flightchess;

import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import com.example.all4win.flightchess.utils.HttpUtil;
import com.example.all4win.flightchess.utils.MessageConsumer;
import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by All4win on 5/3/16.
 */
public class GameActivity extends UnityPlayerActivity {
    private LinearLayout u3dLayout;
    private String RoomId;

    private MessageConsumer mConsumer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Bundle bundle;
        bundle = this.getIntent().getExtras();
        int position[];
        position = bundle.getIntArray("Position");
        RoomId = bundle.getString("RoomId");

        mConsumer = new MessageConsumer("172.18.40.94", "amq.fanout", "fanout");
        new consumerconnect().execute();

        mConsumer.setOnReceiveMessageHandler(new MessageConsumer.OnReceiveMessageHandler() {

            public void onReceiveMessage(byte[] message) {
                String text = "";
                String temp = "";
                try {
                    text = new String(message, "UTF8");
                    Log.e("hello", text);

                    JSONObject jsonObject = new JSONObject(text);
                    if (jsonObject.has("State")) {
                        if (jsonObject.get("State").equals("3")) {
                            temp = temp + jsonObject.get("User") + ";" + jsonObject.get("Flight")
                                    + ";" + jsonObject.get("Num");
                            setNext(temp);
                        }
                    }

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        String sg = position[0]+ ";" + position[1] + ";" + position[2] + ";" + + position[3];
        startGame(sg);
        u3dLayout = (LinearLayout) findViewById(R.id.unityViewLayout);
        u3dLayout.addView(mUnityPlayer);
        mUnityPlayer.requestFocus();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // 什么都不用写
        }
        else {
            // 什么都不用写
        }
    }
    public void startGame(String s){
        UnityPlayer.UnitySendMessage("Manager", "startGame", s);
    }

    public void finishGame(){
        //被调用结束游戏
        u3dLayout.removeAllViews();

        GameActivity.this.finish();
    }

    public void SendNext(int user, int flight, int num){
        //HTTP上传 被调用
        Log.w("game", "我被调用了");
        SendTask sendTask = new SendTask(user, flight, num);
        sendTask.execute();
    }

    public void setNext(String s){
        UnityPlayer.UnitySendMessage("Manager", "SetNext", s);
    }

    private class SendTask extends AsyncTask<String, Integer, Void> {

        private int user;
        private int flight;
        private int num;
        SendTask(int a, int b, int c){
            user = a;
            flight = b;
            num = c;
        }
        @Override
        protected Void doInBackground(String... params) {
            Map<String, String> map = new HashMap<>();
            map.put("RoomId", RoomId);
            map.put("User", user+"");
            map.put("Flight", flight+"");
            map.put("Num", num + "");
            HttpUtil.submitPostDataForRoom(map, "utf-8",5);
            return null;
        }

    }

    private class consumerconnect extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... Message) {
            try {
                // Connect to broker
                mConsumer.connectToRabbitMQ();

            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
            // TODO Auto-generated method stub
            return null;
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        new consumerconnect().execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mConsumer.dispose();
    }
}
