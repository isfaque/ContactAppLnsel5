package apps.lnsel.com.contactapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import apps.lnsel.com.contactapp.VolleyLibrary.AppController;

/**
 * Created by apps2 on 7/7/2017.
 */
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "REQ_LOGIN";

    private EditText et_username, et_password;

    private TextInputLayout til_username, til_password;

    Button btn_login, btn_signup;

    String usrUserName, usrPassword;

    SharedManagerUtil session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        session = new SharedManagerUtil(this);

        btn_login = (Button) findViewById(R.id.activity_login_btn_login);
        btn_signup = (Button) findViewById(R.id.activity_login_btn_signup);

        til_username = (TextInputLayout) findViewById(R.id.activity_login_til_username);
        til_password = (TextInputLayout) findViewById(R.id.activity_login_til_password);

        et_username = (EditText) findViewById(R.id.activity_login_et_username);
        et_password = (EditText) findViewById(R.id.activity_login_et_password);

        et_username.addTextChangedListener(new MyTextWatcher(et_username));
        et_password.addTextChangedListener(new MyTextWatcher(et_password));

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateUsername()) {
                    return;
                }
                if (!validatePassword()) {
                    return;
                }

                usrUserName = et_username.getText().toString().trim();
                usrPassword = et_password.getText().toString().trim();

                loginWebService();

            }
        });

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signup = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(signup);
            }

        });
    }

    public void loginWebService(){

        String url = WebServiceUrls.LOGIN_URL;

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {

                        String str_response = response;

                        try {
                            JSONObject jsonObj = new JSONObject(str_response);
                            String status = jsonObj.getString("status");
                            String message = jsonObj.getString("message");
                            if(status.equals("failed")){
                                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                            }else{
                                String usrId = jsonObj.getString("usrId");
                                String usrUserName = jsonObj.getString("usrUserName");
                                String usrPassword = jsonObj.getString("usrPassword");
                                session.createLoginSession(usrId, usrUserName, usrPassword);
                                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                        Toast.makeText(LoginActivity.this, "Server not Responding, Please Check your Internet Connection", Toast.LENGTH_LONG).show();

                    }
                }
        ){
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("usrUserName",usrUserName);
                params.put("usrPassword",usrPassword);
                return params;
            }
        };

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(postRequest);

    }


    //********** Text Watcher for Validation *******************//
    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.activity_login_et_username:
                    validateUsername();
                    break;
                case R.id.activity_login_et_password:
                    validatePassword();
                    break;
            }
        }
    }



    private boolean validateUsername() {
        if (et_username.getText().toString().trim().isEmpty()) {
            til_username.setError("Please enter username");
            requestFocus(et_username);
            return false;
        } else {
            til_username.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword() {
        if (et_password.getText().toString().trim().isEmpty()) {
            til_password.setError("Please enter password");
            requestFocus(et_password);
            return false;
        } else {
            til_password.setErrorEnabled(false);
        }

        return true;
    }


    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}
