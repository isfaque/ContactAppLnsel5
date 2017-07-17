package apps.lnsel.com.contactapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import apps.lnsel.com.contactapp.VolleyLibrary.AppController;

public class MainActivity extends AppCompatActivity {

    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;

    ContactsBaseAdapter adapter;

    SharedManagerUtil session;

    FloatingActionButton fabtn_add_contact;

    Button btn_clear;

    private ProgressDialog progress;

    ListView list;
    EditText et_contact_search;

    private static final String TAG = "REQ_GET_CONTACTS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new SharedManagerUtil(this);

        fabtn_add_contact = (FloatingActionButton) findViewById(R.id.activity_main_fabtn_add_contact);
        list = (ListView) findViewById(R.id.list_view);
        et_contact_search = (EditText) findViewById(R.id.activity_main_et_contact_search);
        btn_clear = (Button) findViewById(R.id.activity_main_btn_clear);
        fabtn_add_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddContactActivity.class);
                startActivity(intent);
            }
        });

        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_contact_search.setText("");
            }
        });

        if(isNetworkAvailable()){
            progress = new ProgressDialog(this);
            progress.setMessage("loading...");
            progress.show();
            progress.setCanceledOnTouchOutside(false);

            getContactsService();

        }else {
            Toast.makeText(MainActivity.this,"Internet not Available", Toast.LENGTH_LONG).show();
        }



    }


    public void getContactsService() {

        String url = WebServiceUrls.GET_CONTACTS_URL+"?usrId="+session.getUserID() ;

        StringRequest req = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d(TAG, response.toString());

                        String str_response = response;

                        ContactsData.contactsList.clear();

                        try {
                            JSONObject jsonObj = new JSONObject(str_response);

                            String status = jsonObj.getString("status");
                            String message = jsonObj.getString("message");

                            if(status.equals("failed")){
                                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                            }else{
                                JSONArray data = jsonObj.getJSONArray("data");
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject e = data.getJSONObject(i);

                                    String cntId = e.getString("cntId");
                                    String cntPersonName = e.getString("cntName");
                                    String cntContactNo = e.getString("cntNumber");
                                    String cntEmail = e.getString("cntEmail");
                                    String cntAddress = e.getString("cntAddress");
                                    String cntStatus = e.getString("cntStatus");



                                    ContactsSetterGetter wp = new ContactsSetterGetter(cntId, cntPersonName, cntContactNo, cntEmail, cntAddress, cntStatus);

                                    // Binds all strings into an array
                                    ContactsData.contactsList.add(wp);

                                }

                                startGetContacts();

                            }



                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(MainActivity.this, "Server not Responding, Please check your Internet Connection", Toast.LENGTH_LONG).show();
            }
        });

        AppController.getInstance().addToRequestQueue(req);

    }

    public void startGetContacts() {
        progress.dismiss();
        adapter=new ContactsBaseAdapter(this, ContactsData.contactsList);
        list.setAdapter(adapter);

        et_contact_search.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                // When user changed the Text
                String text = et_contact_search.getText().toString().toLowerCase(Locale.getDefault());
                adapter.filter(text, btn_clear);

            }
        });


    }

    /** function for options menu **/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    /** function for item selected in options menu **/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                // action with ID action_refresh was selected
                case R.id.logout:
                    logoutDialog();
                    break;
                case R.id.about:
                    Toast.makeText(MainActivity.this, "About Selected", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    break;
            }

        return super.onOptionsItemSelected(item);
    }

    private void logoutDialog(){
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setMessage("Do you want to Logout from app")
                .setTitle("Logout")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                       //Yes Selected
                        session.logoutUser();
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        //No Selected
                        dialog.cancel();
                    }
                });
        final android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onBackPressed() {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis())
        {
            super.onBackPressed();
            return;
        }
        else { Toast.makeText(getBaseContext(), "Press once again to exit!", Toast.LENGTH_SHORT).show(); }

        mBackPressed = System.currentTimeMillis();
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}

