package uminho.qrcode;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.SecureRandom;


public class Register extends AppCompatActivity {
    ProgressBar progressBar2;
    Button register;
    Intent returnIntent;
    EditText email, pword1, pword2;
    SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        new AlertDialog.Builder(this)
                .setTitle("License agreement")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            //Request permission
                            ActivityCompat.requestPermissions(Register.this,
                                    new String[]{Manifest.permission.CAMERA}, 1);
                            return;
                        }
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED) {
                            //Request permission
                            ActivityCompat.requestPermissions(Register.this,
                                    new String[]{Manifest.permission.VIBRATE}, 2);
                            return;
                        }
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            //Request permission
                            ActivityCompat.requestPermissions(Register.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 3);
                            return;
                        }
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            //Request permission
                            ActivityCompat.requestPermissions(Register.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 4);
                            return;
                        }
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                            //Request permission
                            ActivityCompat.requestPermissions(Register.this,
                                    new String[]{Manifest.permission.INTERNET}, 5);
                            return;
                        }
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
                            //Request permission
                            ActivityCompat.requestPermissions(Register.this,
                                    new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, 2);
                            return;
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }
                })
                .setMessage(R.string.terms_conditions)
                .show();

        pref = getApplicationContext().getSharedPreferences("file009", MODE_PRIVATE);

        progressBar2 = (ProgressBar) findViewById(R.id.progressBar2);
        returnIntent = new Intent();

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
                returnIntent.putExtra("first","true");
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }else{
                returnIntent.putExtra("first","false");
                setResult(Activity.RESULT_OK,returnIntent);
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
                    byte[] salt         = Cripto.getSalt(null);
                    String securehash   = Cripto.generateStorngPasswordHash(p1, salt);
                    MessageDigest md    = MessageDigest.getInstance("SHA-256");
                    md.update(securehash.getBytes());
                    String secrethash   = Cripto.toHex(md.digest());
                    md.update(secrethash.getBytes());
                    String hash         = Cripto.toHex(md.digest());

                    SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
                    byte[] iv = new byte[16];
                    sr.nextBytes(iv);
                    pref.edit().putString("iv", Cripto.toHex(iv)).commit();
                    pref.edit().putString("code", hash).commit();
                    pref.edit().putString("salt", Cripto.toHex(salt)).commit();


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