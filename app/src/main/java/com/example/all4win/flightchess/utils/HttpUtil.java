package com.example.all4win.flightchess.utils;

import android.util.Log;

import com.example.all4win.flightchess.Room;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpUtil {
    //申明base url
//    public static final String Room_URL = "http://172.18.41.97:8080/FlightChess/queryRoom";
//    public static final String Create_Room_URL = "http://172.18.41.97:8080/FlightChess/createRoom";
//    public static final String Enter_Room_URL = "http://172.18.41.97:8080/FlightChess/enterRoom";
//    private static final String Login_URL = "http://172.18.41.97:8080/FlightChess/login";
//    private static final String Register_URL = "http://172.18.41.97:8080/FlightChess/register";
//    private static final String Heart_URL = "http://172.18.41.97:8080/FlightChess/message";
//    private static final String Quit_URL = "http://172.18.41.97:8080/FlightChess/quitRoom";
//    private static final String Start_URL = "http://172.18.41.97:8080/FlightChess/startGame";

    public static final String Room_URL = "http://172.18.40.94:8080/FlightChess/queryRoom";
    public static final String Create_Room_URL = "http://172.18.40.94:8080/FlightChess/createRoom";
    public static final String Enter_Room_URL = "http://172.18.40.94:8080/FlightChess/enterRoom";
    private static final String Login_URL = "http://172.18.40.94:8080/FlightChess/login";
    private static final String Register_URL = "http://172.18.40.94:8080/FlightChess/register";
    private static final String Send_URL = "http://172.18.40.94:8080/FlightChess/message";
    private static final String Quit_URL = "http://172.18.40.94:8080/FlightChess/quitRoom";
    private static final String Start_URL = "http://172.18.40.94:8080/FlightChess/startGame";

    private static final int Login_ID = 1;
    private static final int Register_ID = 2;
    private static final int Create_Room_ID = 3;
    private static final int Enter_Room_ID = 4;
    private static final int Send_ID = 5;
    private static final int Quit_Room_ID= 6;
    private static final int Start_Game_ID = 7;
    //通过url获得HttpGet对象
    public static List<Room> queryRoom() {
        List<Room> list = new ArrayList<Room>();
        String ans = "";
        try {
            URL theUrl = new URL(Room_URL);
            HttpURLConnection urlConnection = (HttpURLConnection)theUrl.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(4000);
            urlConnection.setReadTimeout(4000);
            urlConnection.connect();
            InputStream is = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer sb=new StringBuffer();
            while((line=br.readLine())!=null){
                sb.append(line);
            }
            ans = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        int temp_room_id;
        int cur_temp;
        JSONObject jsonObject_temp;
        try {
            JSONArray jsonArray = new JSONArray(ans);
            for (int i = 0; i < jsonArray.length(); i ++) {
                jsonObject_temp = jsonArray.optJSONObject(i);
                cur_temp = Integer.parseInt(jsonObject_temp.getString("PlayerNum"));
                temp_room_id = Integer.parseInt(jsonObject_temp.getString("RoomId"));
                list.add(new Room(cur_temp, temp_room_id));
            }
        } catch (JSONException e) {
            list.clear();
            //e.printStackTrace();
        }
        return list;
    }


    /*
         * Function  :   发送Post请求到服务器
         * Param     :   params请求体内容，encode编码格式
         */
    public static Map<String,String> submitPostDataForRegisterAndLogin(Map<String, String> params, String encode, int mode) {
        Map<String,String> map = new HashMap<>();
        byte[] data = getRequestData(params, encode).toString().getBytes();//获得请求体
        try {
            URL url;
            if (mode == Register_ID){
                url = new URL(Register_URL);
            }else {
                url = new URL(Login_URL);
            }

            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setConnectTimeout(3000);     //设置连接超时时间
            httpURLConnection.setDoInput(true);                  //打开输入流，以便从服务器获取数据
            httpURLConnection.setDoOutput(true);                 //打开输出流，以便向服务器提交数据
            httpURLConnection.setRequestMethod("POST");     //设置以Post方式提交数据
            httpURLConnection.setUseCaches(false);               //使用Post方式不能使用缓存
            //设置请求体的类型是文本类型
            //httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            //设置请求体的长度

            httpURLConnection.setRequestProperty("Content-Length", String.valueOf(data.length));
            httpURLConnection.setRequestProperty("Accept", "application/json");
            //获得输出流，向服务器写入数据
            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(data);

            int response = httpURLConnection.getResponseCode();            //获得服务器的响应码
            //Log.v("Httpresponse", response + "");
            if(response == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = httpURLConnection.getInputStream();
                return dealResponseResult(inputStream, mode);                     //处理服务器的响应结果
            }
        } catch (IOException e) {
            //e.printStackTrace();
            map.put("State", "Error");
            return map;
        }
        map.put("State", "Error");
        return map;
    }




    public static Map<String,String> submitPostDataForRoom(Map<String, String> params, String encode, int mode) {
        Map<String,String> map = new HashMap<>();
        byte[] data = getRequestData(params, encode).toString().getBytes();//获得请求体
        try {
            URL url;
            if (mode == Create_Room_ID){
                url = new URL(Create_Room_URL);
            }else if (mode == Enter_Room_ID){
                url = new URL(Enter_Room_URL);
            }else if (mode == Quit_Room_ID){
                url = new URL(Quit_URL);
            }else if (mode == Start_Game_ID){
                url = new URL(Start_URL);
            }else {
                url = new URL(Send_URL);
            }

            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setConnectTimeout(3000);     //设置连接超时时间
            httpURLConnection.setDoInput(true);                  //打开输入流，以便从服务器获取数据
            httpURLConnection.setDoOutput(true);                 //打开输出流，以便向服务器提交数据
            httpURLConnection.setRequestMethod("POST");     //设置以Post方式提交数据
            httpURLConnection.setUseCaches(false);               //使用Post方式不能使用缓存
            //设置请求体的类型是文本类型
            //httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            //设置请求体的长度

            httpURLConnection.setRequestProperty("Content-Length", String.valueOf(data.length));
            httpURLConnection.setRequestProperty("Accept", "application/json");
            //获得输出流，向服务器写入数据
            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(data);

            int response = httpURLConnection.getResponseCode();            //获得服务器的响应码
            //Log.v("Httpresponse", response + "");
            if(response == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = httpURLConnection.getInputStream();
                return dealResponseResult(inputStream, mode);                     //处理服务器的响应结果
            }
        } catch (IOException e) {
            //e.printStackTrace();
            map.put("State", "Error");
            return map;
        }
        map.put("State", "Error");
        return map;
    }


//    public static Map<String,String> postHeartPacket() {
//        Map<String,String> map = new HashMap<>();
//        byte[] data = getRequestData(map, "utf-8").toString().getBytes();//获得请求体
//        try {
//            URL url;
//
//            url = new URL(Heart_URL);
//
//            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
//            httpURLConnection.setConnectTimeout(3000);     //设置连接超时时间
//            httpURLConnection.setDoInput(true);                  //打开输入流，以便从服务器获取数据
//            httpURLConnection.setDoOutput(true);                 //打开输出流，以便向服务器提交数据
//            httpURLConnection.setRequestMethod("POST");     //设置以Post方式提交数据
//            httpURLConnection.setUseCaches(false);               //使用Post方式不能使用缓存
//            //设置请求体的类型是文本类型
//            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
//            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//            //设置请求体的长度
//            httpURLConnection.setRequestProperty("Content-Length", String.valueOf(data.length));
//            httpURLConnection.setRequestProperty("Accept", "application/json");
//            //获得输出流，向服务器写入数据
//            OutputStream outputStream = httpURLConnection.getOutputStream();
//            outputStream.write(data);
//
//            int response = httpURLConnection.getResponseCode();            //获得服务器的响应码
//            //Log.v("Httpresponse", response + "");
//            if(response == HttpURLConnection.HTTP_OK) {
//                InputStream inputStream = httpURLConnection.getInputStream();
//                return dealResponseResult(inputStream, 5);                     //处理服务器的响应结果
//            }
//        } catch (IOException e) {
//            //e.printStackTrace();
//            map.put("State", "Error");
//            return map;
//        }
//        map.put("State", "Error");
//        return map;
//    }







    /*
     * Function  :   封装请求体信息
     * Param     :   params请求体内容，encode编码格式
     */
    public static StringBuffer getRequestData(Map<String, String> params, String encode) {
        StringBuffer stringBuffer = new StringBuffer();        //存储封装好的请求体信息
        try {
            for(Map.Entry<String, String> entry : params.entrySet()) {
                stringBuffer.append(entry.getKey())
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), encode))
                        .append("&");
            }
            if (params.size()!=0){
                stringBuffer.deleteCharAt(stringBuffer.length() - 1);    //删除最后的一个"&"
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuffer;
    }

    /*
     * Function  :   处理服务器的响应结果（将输入流转化成字符串）
     * Param     :   inputStream服务器的响应输入流
     */
    public static Map<String,String> dealResponseResult(InputStream inputStream, int mode) {
        Map<String, String> map = new HashMap<>();
        map.put("State", "Error");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        try {
            while((len = inputStream.read(data)) != -1) {
                byteArrayOutputStream.write(data, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String str;

        str = new String(byteArrayOutputStream.toByteArray());
        Log.e("abcdef", str);
        System.out.print(str);
        //Log.v("length", str.length() + "");


            try {
                String temp;

                JSONObject jsonObject = new JSONObject(str);
                    if (jsonObject.has("State")){
                        temp = jsonObject.getString("State");
                        map.put("State", temp);
                    }
                    if (mode == Login_ID || mode == Register_ID){
                        if (jsonObject.has("UserName")){
                            temp = jsonObject.getString("UserName");
                            map.put("UserName", temp);
                        }
                        if (jsonObject.has("UserId")){
                            temp = jsonObject.getString("UserId");
                            map.put("UserId", temp);
                        }
                    }
                    else if (mode == Create_Room_ID || mode == Enter_Room_ID ||mode == Quit_Room_ID) {
                        if (jsonObject.has("RoomId")) {
                            temp = jsonObject.getString("RoomId");
                            map.put("RoomId", temp);
                        }
                        if (jsonObject.has("Host")) {
                            temp = jsonObject.getString("Host");
                            map.put("Host", temp);
                        }
                        if (jsonObject.has("Player1")) {
                            temp = jsonObject.getString("Player1");
                            map.put("Player1", temp);
                            temp = jsonObject.getString("Player2");
                            map.put("Player2", temp);
                            temp = jsonObject.getString("Player3");
                            map.put("Player3", temp);
                            temp = jsonObject.getString("Player4");
                            map.put("Player4", temp);
                        }
                        if (jsonObject.has("Position")) {
                            temp = jsonObject.getString("Position");
                            map.put("Position", temp);
                        }
                    }


            } catch (JSONException e) {
                map.put("State", "Error");
                //e.printStackTrace();
            }
        return map;
    }
}
