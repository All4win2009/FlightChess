package com.example.all4win.flightchess;
/**
 * Created by All4win on 3/30/16.
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.all4win.flightchess.utils.CheckNetwork;
import com.example.all4win.flightchess.utils.HttpUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoomActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private List<Room> roomList;
    private Button button;
    private Button quitButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        button = (Button)findViewById(R.id.create_room_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateTask createTask = new CreateTask();
                createTask.execute();
            }
        });
        quitButton = (Button)findViewById(R.id.quit_login);
        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor = sharedPreferences.edit();
                editor.putBoolean("login_state", false);
                editor.commit();
                Intent intent = new Intent(RoomActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(this);
        listView = (ListView)findViewById(R.id.room_list_view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Room room = (Room) parent.getItemAtPosition(position);
                String room_id = room.getRoomNumber() + "";
                EnterTask enterTask = new EnterTask(room_id);
                enterTask.execute();
            }
        });
        MyAsyncTask myAsyncTask = new MyAsyncTask();
        myAsyncTask.execute();

    }

    //刷新房间列表
    private class MyAsyncTask extends AsyncTask<String , Integer, List<Room>> {

        @Override
        protected List<Room> doInBackground(String... params) {
            return HttpUtil.queryRoom();
        }

        @Override
        protected void onPostExecute(List<Room> l) {
            super.onPostExecute(l);

            if (l.size() >= 0){
                roomList = new ArrayList<Room>(l);
                RoomAdapter roomAdapter = new RoomAdapter(RoomActivity.this, R.layout.room_item, roomList);
                listView.setAdapter(roomAdapter);
            }
        }
    }

    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                MyAsyncTask myAsyncTask = new MyAsyncTask();
                myAsyncTask.execute();
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 1000);
    }

    //创建房间
    private class CreateTask extends AsyncTask<String , Integer, Map<String,String>> {

        @Override
        protected Map<String,String> doInBackground(String... params) {
            Map<String, String> map = new HashMap<>();

            map.put("UserId", sharedPreferences.getString("user_id", "-1"));
            return HttpUtil.submitPostDataForRoom(map, "utf-8", 3);
        }

        @Override
        protected void onPostExecute(Map<String,String> m) {
            super.onPostExecute(m);

            boolean flag = CheckNetwork.isConnected(RoomActivity.this);

            if (!flag ||m.get("State").equals("Error")){
                Toast.makeText(RoomActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
            }
            if (m.get("State").equals("Yes")){
                Toast.makeText(RoomActivity.this, "创建成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RoomActivity.this, ReadyActivity.class);
                //TODO
                Bundle bundle = new Bundle();
                bundle.putString("method", "Create");
                bundle.putString("room_id",m.get("RoomId"));
                intent.putExtras(bundle);
                RoomActivity.this.startActivity(intent);
            }
            else if (m.get("State").equals("No")){
                Toast.makeText(RoomActivity.this, "创建失败", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private class EnterTask extends AsyncTask<String , Integer, Map<String,String>> {
        String myRoom;

        EnterTask(String room){
            myRoom = room;
        }
        @Override
        protected Map<String,String> doInBackground(String... params) {
            Map<String, String> map = new HashMap<>();
            map.put("RoomId", myRoom);
            map.put("UserId", sharedPreferences.getString("user_id", "-1"));
            return HttpUtil.submitPostDataForRoom(map, "utf-8", 4);
        }

        @Override
        protected void onPostExecute(Map<String,String> m) {
            super.onPostExecute(m);

            boolean flag = CheckNetwork.isConnected(RoomActivity.this);

            if (!flag ||m.get("State").equals("Error")){
                Toast.makeText(RoomActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
            }
            if (m.get("State").equals("Yes")){
                Toast.makeText(RoomActivity.this, "加入成功", Toast.LENGTH_SHORT).show();
                Bundle bundle = new Bundle();
                bundle.putString("room_id",m.get("RoomId"));
                bundle.putString("Player1", m.get("Player1"));
                bundle.putString("Player2", m.get("Player2"));
                bundle.putString("Player3", m.get("Player3"));
                bundle.putString("Player4", m.get("Player4"));
                bundle.putString("Host", m.get("Host"));
                bundle.putString("Position", m.get("Position"));
                bundle.putString("method", "Enter");
                Intent intent = new Intent(RoomActivity.this, ReadyActivity.class);
                intent.putExtras(bundle);
                RoomActivity.this.startActivity(intent);
            }
            else if (m.get("State").equals("No")){
                Toast.makeText(RoomActivity.this, "加入失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
