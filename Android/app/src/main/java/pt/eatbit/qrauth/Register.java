package pt.eatbit.qrauth;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


public class Register extends AppCompatActivity {
    ProgressBar progressBar2;
    Button register;
    EditText fname, lname, email, pword1, pword2;
    SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getApplicationContext().getSharedPreferences("prefinfo", MODE_PRIVATE);

        setContentView(R.layout.activity_register);
        progressBar2 = (ProgressBar) findViewById(R.id.progressBar2);

        progressBar2.setVisibility(View.GONE);

        register    = (Button) findViewById(R.id.button2);

        pword1       = (EditText) findViewById(R.id.password1);
        pword2       = (EditText) findViewById(R.id.password2);



        register.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                String p1 = pword1.getText().toString();
                String p2 = pword2.getText().toString();

                Register.newRegistration newregistration = new Register.newRegistration();
                newregistration.execute(p1, p2);
            }
        });
    }

    public class newRegistration extends AsyncTask<String,String,String>
    {
        String z = "";
        Boolean isSuccess = false;

        @Override
        protected void onPreExecute()
        {
            progressBar2.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r)
        {
            progressBar2.setVisibility(View.GONE);
            Toast.makeText(Register.this, r, Toast.LENGTH_SHORT).show();
            if(isSuccess)
            {
                Toast.makeText(Register.this , "Registration Successfull" , Toast.LENGTH_LONG).show();
            }
        }
        @Override
        protected String doInBackground(String... params)
        {
            String p1    = params[0];
            String p2    = params[1];

            if(p1.trim().equals("")|| p2.trim().equals("") )
                z = "Please fill in all parameters";

            else if(!p1.equals(p2))
                z = "Passwords must coincide";
            else if(p1.length() < 4 )
                z = "Pass at least 4 chars long";
            else
            {
                try
                {
                    pref.edit().putString("setup", "true").commit();
                    pref.edit().remove("code");
                    //METER PBKDF2.....
                    pref.edit().putString("code", p1).commit();

                    String result = pref.getString("code", "0");
                    if(result != "0")
                    {
                        z = "Registered successfully";
                        isSuccess=true;
                    }
                    else
                    {
                        z = "oops , something went wrong";
                        isSuccess = false;
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