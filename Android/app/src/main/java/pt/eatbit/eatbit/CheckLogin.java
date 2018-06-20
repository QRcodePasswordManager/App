package pt.eatbit.eatbit;

import android.os.AsyncTask;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by sousa on 08-06-2017.
 */

public class CheckLogin extends AsyncTask<String,String,String>
{

    String      z = "";
    Boolean     isSuccess = false;
    ProgressBar progressBar;
    String usr, pass;
    MSConnect   connection;
    TextView    message;


    public CheckLogin(ProgressBar pb){
        this.progressBar    = pb;
    }

    @Override
    protected  void onPreExecute()
    {
        this.connection = new MSConnect();
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostExecute(String r)
    {
        progressBar.setVisibility(View.GONE);
        message.setVisibility(View.VISIBLE);
        message.setText(z);

        if(isSuccess)
        {
           message.setVisibility(View.VISIBLE);

            //finish();
        }
    }

    @Override
    protected String doInBackground(String... params)
    {
        String usernam      = params[0];
        String passwordd    = params[1];
        if(usernam.trim().equals("")|| passwordd.trim().equals(""))
            z = "Please enter Username and Password";
        else
            try {
                MSConnect   cons = new MSConnect();
                Connection con = cons.connect();
                if (connection == null) {
                    z = "Check Your Internet Access!";
                } else {
                    // Change below query according to your own database.
                    String query = "select * from login where user_name= '" + usernam.toString() + "' and pass_word = '" + passwordd.toString() + "'  ";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    if (rs.next()) {
                        z = "Login successful";
                        isSuccess = true;
                        con.close();
                    } else {
                        z = "Invalid Credentials!";
                        isSuccess = false;
                    }
                }
            } catch (Exception ex) {
                isSuccess = false;

                z = ex.getMessage();

            }
        return z;
    }
}