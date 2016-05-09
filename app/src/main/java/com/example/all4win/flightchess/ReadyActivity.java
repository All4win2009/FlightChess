package com.example.all4win.flightchess;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.all4win.flightchess.utils.CheckNetwork;
import com.example.all4win.flightchess.utils.HttpUtil;
import com.example.all4win.flightchess.utils.MessageConsumer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class ReadyActivity extends AppCompatActivity {
    private Button button;
    private TextView player1;
    private TextView player2;
    private TextView player3;
    private TextView player4;
    private SharedPreferences sharedPreferences;
    private String roomID;
    private boolean isHost;

    private int pos;

    private MessageConsumer mConsumer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ready);

        Bundle bundle;
        bundle = this.getIntent().getExtras();

        String method = bundle.getString("method");
        roomID = bundle.getString("room_id");
        isHost = false;
        pos = 0;
        String temp;
        player1 = (TextView)findViewById(R.id.player_1);
        player2 = (TextView)findViewById(R.id.player_2);
        player3 = (TextView)findViewById(R.id.player_3);
        player4 = (TextView)findViewById(R.id.player_4);
        sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
        if (method.equals("Create")){
            temp = sharedPreferences.getString("user_name", "No Player");
            player1.setText(temp);
            isHost = true;
        }
        else if (method.equals("Enter")){
            temp = bundle.getString("Player1");
            player1.setText(temp);
            temp = bundle.getString("Player2");
            player2.setText(temp);
            temp = bundle.getString("Player3");
            player3.setText(temp);
            temp = bundle.getString("Player4");
            player4.setText(temp);
            temp = bundle.getString("Host");
            if (temp.equals(sharedPreferences.getString("user_name", "-1"))){
                isHost = true;
            }
            temp = bundle.getString("Position");
            pos = Integer.parseInt(temp);
        }

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        button = (Button)findViewById(R.id.start_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isHost){
                    StartTask startTask = new StartTask();
                    startTask.execute();
                }
                else {
                    Toast.makeText(ReadyActivity.this, "只有房主才能开始游戏~", Toast.LENGTH_SHORT).show();
                }

            }
        });
        //PollingUtils.startPollingService(ReadyActivity.this, 1, PollingService.class, PollingService.ACTION);

        mConsumer = new MessageConsumer("172.18.40.94", "amq.fanout", "fanout");
        new consumerconnect().execute();

        mConsumer.setOnReceiveMessageHandler(new MessageConsumer.OnReceiveMessageHandler() {

            public void onReceiveMessage(byte[] message) {
                String text = "";
                String temp;
                try {
                    text = new String(message, "UTF8");
                    Log.e("hello", text);

                    JSONObject jsonObject = new JSONObject(text);
                    if (jsonObject.has("State")){
                        if (jsonObject.get("State").equals("1")){
                            player1.setText(jsonObject.get("Player1").toString());
                            player2.setText(jsonObject.get("Player2").toString());
                            player3.setText(jsonObject.get("Player3").toString());
                            player4.setText(jsonObject.get("Player4").toString());
                            temp = jsonObject.get("Host").toString();
                            if (temp.equals(sharedPreferences.getString("user_name", "-1"))){
                                isHost = true;
                            }
                            else {
                                isHost = false;
                            }
                        }
                        else if (jsonObject.get("State").equals("2")){
                            Bundle gameBundle = new Bundle();
                            int position[] = {2,2,2,2};
                            position[pos] = 1;
                            gameBundle.putIntArray("Position", position);
                            gameBundle.putString("RoomId", roomID);
                            Intent intent = new Intent(ReadyActivity.this, GameActivity.class);
                            intent.putExtras(gameBundle);
                            startActivity(intent);
                        }
                    }

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        //PollingUtils.stopPollingService(ReadyActivity.this, PollingService.class,PollingService.ACTION);
        QuitTask quitTask = new QuitTask();
        quitTask.execute();
        super.onDestroy();
    }

    private class QuitTask extends AsyncTask<String , Integer, Map<String,String>> {

        @Override
        protected Map<String,String> doInBackground(String... params) {
            Map<String, String> map = new HashMap<>();
            map.put("RoomId", roomID);
            map.put("UserId", sharedPreferences.getString("user_id", "-1"));
            return HttpUtil.submitPostDataForRoom(map, "utf-8", 6);
        }

        @Override
        protected void onPostExecute(Map<String,String> m) {
            super.onPostExecute(m);

            boolean flag = CheckNetwork.isConnected(ReadyActivity.this);
            if (!flag || m.get("State").equals("Error")) {
                Toast.makeText(ReadyActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                return;
            }
            if (m.get("State").equals("Yes")){
                ReadyActivity.this.finish();
            }else if (m.get("State").equals("No")){
                Toast.makeText(ReadyActivity.this, "退出失败", Toast.LENGTH_SHORT).show();
            }
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


    private class StartTask extends AsyncTask<String , Integer, Map<String,String>> {

        @Override
        protected Map<String,String> doInBackground(String... params) {
            Map<String, String> map = new HashMap<>();
            map.put("RoomId", roomID);
            map.put("UserId", sharedPreferences.getString("user_id", "-1"));
            return HttpUtil.submitPostDataForRoom(map, "utf-8", 7);
        }

        @Override
        protected void onPostExecute(Map<String,String> m) {
            super.onPostExecute(m);

            boolean flag = CheckNetwork.isConnected(ReadyActivity.this);
            if (!flag || m.get("State").equals("Error")) {
                Toast.makeText(ReadyActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                return;
            }
            if (m.get("State").equals("Yes")) {
                Toast.makeText(ReadyActivity.this, "开始游戏", Toast.LENGTH_SHORT).show();
            }else if (m.get("State").equals("No")){
                Toast.makeText(ReadyActivity.this, "服务器拒绝了你的游戏请求", Toast.LENGTH_SHORT).show();
            }
        }
    }
}