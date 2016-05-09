package com.example.all4win.flightchess;
/**
 * Created by All4win on 3/30/16.
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.all4win.flightchess.utils.CheckNetwork;
import com.example.all4win.flightchess.utils.HttpUtil;
import com.example.all4win.flightchess.utils.ValidateUserInfo;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mUserView;
    private EditText mPasswordView;

    private TextView  txt_account;
    private Button mEmailRegisterButton;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private boolean login_state = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initInstances();
    }

    private void initInstances() {
        sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        login_state = false;


        mUserView = (EditText) findViewById(R.id.edit_name);
        mPasswordView = (EditText) findViewById(R.id.edit_password);

        txt_account = (TextView) findViewById(R.id.txt_already_have);
        txt_account.setOnClickListener(this);

        mEmailRegisterButton = (Button) findViewById(R.id.btn_register);
        mEmailRegisterButton.setOnClickListener(this);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /**
     * Attempts to sign in by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptRegister() {

        // Reset errors.
        mUserView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String name = mUserView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        ValidateUserInfo validateUserInfo = new ValidateUserInfo();

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(name) && !validateUserInfo.isNameValid(name)) {
            mUserView.setError(getString(R.string.error_invalid_password));
            focusView = mUserView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !validateUserInfo.isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(name)) {
            mUserView.setError(getString(R.string.error_field_required));
            focusView = mUserView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            //showProgress(true);

            new MyAsyncTask(name, password).execute();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_already_have:
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.btn_register:
                attemptRegister();
                break;

        }
    }

    private Map<String,String> query(String Name, String Password) {
        Map<String,String> map = new HashMap<String,String>();
        map.put("UserName", Name);
        map.put("Password", Password);
        return HttpUtil.submitPostDataForRegisterAndLogin(map, "utf-8", 2);
    }

    private class MyAsyncTask extends AsyncTask<String, Integer, Map<String,String>> {

        private final String mName;
        private final String mPassword;

        MyAsyncTask(String Name, String password) {
            mName = Name;
            mPassword = password;
        }
        @Override
        protected Map<String,String> doInBackground(String... params) {
            return query(mName, mPassword);
        }

        @Override
        protected void onPostExecute(Map<String,String> m) {
            super.onPostExecute(m);
            String username = "SYSU";
            String userId = "0";
            boolean flag = CheckNetwork.isConnected(RegisterActivity.this);
            if (!flag || m.get("State").equals("Error")) {
                Toast.makeText(RegisterActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                return;
            }


            if (m.get("State").equals("Yes")){
                username = m.get("UserName");
                userId = m.get("UserId");
                Toast.makeText(RegisterActivity.this, "注册成功,欢迎使用", Toast.LENGTH_SHORT).show();
                login_state = true;
                editor.putBoolean("login_state", login_state);
                editor.putString("user_id", userId);
                editor.putString("user_name", username);
                editor.commit();
                Intent intent = new Intent(RegisterActivity.this, RoomActivity.class);
                RegisterActivity.this.startActivity(intent);
                RegisterActivity.this.finish();
            }else if (m.get("State").equals("No")){
                Toast.makeText(RegisterActivity.this, "注册失败,用户名已被注册", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            login_state = false;
        }
    }
}
