package com.example.all4win.flightchess;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;


/**
 * Created by All4win on 5/5/16.
 */
public class PollingService extends Service {
    public static final String ACTION = "com.example.all4win.service.PollingService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
//    public int onStartCommand(Intent intent,int flags, int startId){
//        MyAsyncTask myAsyncTask = new MyAsyncTask();
//        myAsyncTask.execute();
//        return START_NOT_STICKY;
//    }
//
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//    }
//
//    private class MyAsyncTask extends AsyncTask<String, Integer, Map<String,String>> {
//
//        @Override
//        protected Map<String,String> doInBackground(String... params) {
//            return HttpUtil.postHeartPacket();
//        }
//
//        @Override
//        protected void onPostExecute(Map<String,String> m) {
//            super.onPostExecute(m);
//        }
//
//    }

}
