package pt.eatbit.qrauth;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by sousa on 21-06-2018.
 */

public class validLogin extends AsyncTask<Void,String,String>
{
    private String jsonPacket;
    private SharedPreferences pref  = null;
    private Boolean     isSuccess = false;
    private ProgressBar progressBar;
    private String      z = "";
    private TextView message;
    private String password;
    private String username;

    public validLogin(SharedPreferences e, String ... params) {
        this.pref       = e;
        this.username   = params[0];
        this.password   = params[1];
    }

    @Override
    protected  void onPreExecute()
    {
        //progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostExecute(String r)
    {
        progressBar.setVisibility(View.GONE);

        if(isSuccess)
        {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //finish();
                    //search();
                }
            }, 1000);

        }
    }

    @Override
    protected String doInBackground(Void... param)
    {

        if(username.trim().equals("")|| password.trim().equals(""))
            z = "Please enter Username and Password";
        else
            try {
                if (pref.contains(username)) {
                    jsonPacket = pref.getString(username, "null");
                    try {
                        JSONObject response = new JSONObject(jsonPacket);
                    }catch(JSONException e) {

                    }
                    Log.d("CREATE", jsonPacket);
                    z = "Login successful";
                    isSuccess = true;
                } else {
                    z = "Invalid Credentials!";
                    isSuccess = false;
                }

            } catch (Exception ex) {
                isSuccess = false;

                z = ex.getMessage();

            }
        return z;
    }
}