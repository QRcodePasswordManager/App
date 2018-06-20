package pt.eatbit.eatbit;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class Register extends AppCompatActivity {
    ProgressBar progressBar2;
    Button register;
    EditText fname, lname, email, pword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        progressBar2 = (ProgressBar) findViewById(R.id.progressBar2);

        progressBar2.setVisibility(View.GONE);

        register    = (Button) findViewById(R.id.button2);

        fname       = (EditText) findViewById(R.id.firstname);
        lname       = (EditText) findViewById(R.id.lastname);
        email       = (EditText) findViewById(R.id.email);
        pword       = (EditText) findViewById(R.id.password);



        register.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String fnames = fname.getText().toString();
                String lnames = lname.getText().toString();
                String emails = email.getText().toString();
                String pwords = pword.getText().toString();

                Register.newRegistration newregistration = new Register.newRegistration();
                newregistration.execute(fnames,lnames,emails,emails,pwords);
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
            String firstname    = params[0];
            String lastname     = params[1];
            String email        = params[2];
            String Morada       = params[3];
            String Password     = params[4];

            if(firstname.trim().equals("")|| lastname.trim().equals(""))
                z = "Please fill in all parameters";
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
                        String query = "INSERT INTO Utilizador (Nome,Email,Morada,Password,Admin) VALUES ('"+ firstname+lastname +"' , '"+ email + "','"+ Morada +"' , '"+ Password +"', 0);";
                        Statement stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery(query);
                        if(rs.next())
                        {
                            z = "Registered successfully";
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