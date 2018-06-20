package pt.eatbit.eatbit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.os.Handler;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;


public class Login extends AppCompatActivity {
    Button      login;
    EditText    username,password;
    ProgressBar progressBar;
    TextView    register;
    MSConnect   connection;
    String un,pass,db,ip;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connection = new MSConnect();
        setContentView(R.layout.activity_login);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        login       = (Button) findViewById(R.id.button);
        username    = (EditText) findViewById(R.id.username);
        password    = (EditText) findViewById(R.id.password);
        register    = (TextView) findViewById(R.id.register);

        progressBar.setVisibility(View.GONE);

        login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String usernam = username.getText().toString();
                String passwordd = password.getText().toString();
                CheckLogin checkLogin = new CheckLogin();
                checkLogin.execute(usernam, passwordd);
            }
        });

        username.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                username.setText("");
                return false;
            }
        });

        password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                password.setText("");
                return false;
            }
        });


    }


    public void register(View view) {
        Intent intent   = new Intent(this, Register.class);
        startActivity(intent);
    }

    public void search(){
        Intent intent   = new Intent(this, Search.class);
        finish();
        startActivity(intent);
    }


    public class CheckLogin extends AsyncTask<String,String,String>
    {
        String z = "";
        Boolean isSuccess = false;

        @Override
        protected void onPreExecute()
        {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r)
        {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(Login.this, r, Toast.LENGTH_SHORT).show();
            if(isSuccess)
            {
                Toast.makeText(Login.this , "Login Successfull" , Toast.LENGTH_LONG).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                        search();
                    }
                }, 1000);

            }
        }
        @Override
        protected String doInBackground(String... params)
        {
            String usernam = params[0];
            String passwordd = params[1];
            if(usernam.trim().equals("")|| passwordd.trim().equals(""))
                z = "Please enter Username and Password";
            else
            {
                try
                {
                    MSConnect connection = new MSConnect();
                    Connection con   = connection.connect();

                    if (con == null)
                    {
                        z = "Check Your Internet Access!";
                    }
                    else
                    {
                        String query = "select * from Utilizador where Email= '" + usernam.toString() + "' and password = '"+ passwordd.toString() +"'  ";
                        Statement stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery(query);
                        if(rs.next())
                        {
                            z = "Login successful";
                            isSuccess=true;
                            con.close();
                        }
                        else
                        {
                            z = "Invalid Credentials!";
                            isSuccess = false;
                        }
                    }
                }
                catch (Exception ex)
                {
                    isSuccess = false;
                    z = ex.getMessage();
                }
            }
            return z;
        }
    }
}


