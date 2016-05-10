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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mNameView;
    private EditText mPasswordView;

    private TextView txt_create, txt_forgot;
    private Button mNameSignInButton;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private boolean login_state = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initInstances();
    }

    private void initInstances() {
        sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        login_state = sharedPreferences.getBoolean("login_state",false);
        if (login_state == true){
            Intent intent = new Intent(LoginActivity.this, RoomActivity.class);
            startActivity(intent);
            finish();
        }


        mNameView = (EditText) findViewById(R.id.txt_name);
        mPasswordView = (EditText) findViewById(R.id.txt_password);

        txt_create = (TextView) findViewById(R.id.txt_create);
        txt_create.setOnClickListener(this);

        txt_forgot = (TextView) findViewById(R.id.txt_forgot);
        txt_forgot.setOnClickListener(this);

        mNameSignInButton = (Button) findViewById(R.id.name_sign_in_button);
        mNameSignInButton.setOnClickListener(this);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void attemptLogin() {
        if (login_state) {
            return;
        }

        // Reset errors.
        mNameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String name = mNameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        ValidateUserInfo validateUserInfo = new ValidateUserInfo();

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !validateUserInfo.isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(name)) {
            mNameView.setError(getString(R.string.error_field_required));
            focusView = mNameView;
            cancel = true;
        }

        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
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
        String name = mNameView.getText().toString();

        switch (v.getId()) {
            case R.id.name_sign_in_button:
                attemptLogin();
                break;
            case R.id.txt_create:
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }
    private Map<String,String> query(String Name, String Password) {
        Map<String,String> map = new HashMap<String,String>();
        map.put("UserName", Name);
        map.put("Password", Password);
        return HttpUtil.submitPostDataForRegisterAndLogin(map, "utf-8", 1);
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
            boolean flag = CheckNetwork.isConnected(LoginActivity.this);
            if (!flag || m.get("State").equals("Error")) {
                Toast.makeText(LoginActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                return;
            }


            if (m.get("State").equals("Yes")){
                username = m.get("UserName");
                userId = m.get("UserId");
                //Toast.makeText(LoginActivity.this, "登录成功,欢迎使用", Toast.LENGTH_SHORT).show();
                login_state = true;
                editor.putBoolean("login_state", login_state);
                editor.putString("user_id", userId);
                editor.putString("user_name", username);
                editor.commit();
                Intent intent = new Intent(LoginActivity.this, RoomActivity.class);
                LoginActivity.this.startActivity(intent);
                LoginActivity.this.finish();
            }else if (m.get("State").equals("No")){
                Toast.makeText(LoginActivity.this, "Username or password is not correct", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            login_state = false;
        }
    }
}

