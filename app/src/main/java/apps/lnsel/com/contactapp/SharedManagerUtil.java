package apps.lnsel.com.contactapp;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedManagerUtil {
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    public static final String PREF_NAME = "ContactPref";

    // All Shared Preferences Keys
    public static final String IS_LOGIN = "IsLoggedIn";


    public static final String KEY_USER_ID = "userId";
    public static final String KEY_USER_NAME = "userName";
    public static final String KEY_USER_PASSWORD = "userPassword";


    // Constructor
    public SharedManagerUtil(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(String userId, String userName, String userPassword){
        editor.putBoolean(IS_LOGIN, true);

        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USER_NAME, userName);
        editor.putString(KEY_USER_PASSWORD, userPassword);

        editor.commit();
    }


    public void logoutUser(){
        editor.clear();
        editor.commit();
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }

    public String getUserID(){
        return pref.getString(KEY_USER_ID, null);
    }

    public String getUserName(){
        return pref.getString(KEY_USER_NAME, null);
    }

    public String getUserPassword(){
        return pref.getString(KEY_USER_PASSWORD, null);
    }

}