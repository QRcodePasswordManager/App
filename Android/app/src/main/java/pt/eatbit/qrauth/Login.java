package pt.eatbit.qrauth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class Login extends AppCompatActivity {
    Button login;
    EditText username, password;
    ProgressBar progressBar;
    TextView register;
    private boolean busy = false;
    private SharedPreferences pref;
    private JSONObject response = null;
    private String jsonPacket;
    private List accounts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //para correr o nosso codigo e nao fazer override do codigo do parent
        super.onCreate(savedInstanceState);
        pref = getApplicationContext().getSharedPreferences("prefinfo", MODE_PRIVATE);
        setContentView(R.layout.activity_login);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        login = (Button) findViewById(R.id.button);
        password = (EditText) findViewById(R.id.password);

        progressBar.setVisibility(View.GONE);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(busy == false){
                    busy = true;
                    String passwordd = password.getText().toString();
                    new ValidateLogin(pref, progressBar, passwordd).execute();
                }else{
                    return;
                }

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
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }

    public void menu() {
        Bundle bundle = new Bundle();
        Intent intent = new Intent(this, Menu.class);

        Map<String,?> keys = pref.getAll();
        keys.remove("setup");

        for(Map.Entry<String,?> entry : keys.entrySet()){
            Log.d("map values",entry.getKey() + ": " +
                    entry.getValue().toString());
        }
        bundle.putSerializable("accounts", (Serializable) keys);
        intent.putExtras(bundle);
        //finish();
        startActivity(intent);

    }


    public class ValidateLogin extends AsyncTask<Void, String, String> {
        private SharedPreferences pref = null;
        private Boolean isSuccess = false;
        private ProgressBar progressBar;
        private String z = "";
        private TextView message;
        private String password;

        public ValidateLogin(SharedPreferences e, ProgressBar p, String... params) {
            this.progressBar = p;
            this.password = params[0];
            this.pref = e;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {
            progressBar.setVisibility(View.GONE);

            if (isSuccess) {
                Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_LONG).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //finish();
                        menu();
                    }
                }, 1000);
            }else{
                Toast.makeText(Login.this, "Login failed: "+r, Toast.LENGTH_LONG).show();
            }

            busy = false;
        }

        @Override
        protected String doInBackground(Void... param) {
            if (password.trim().equals(""))
                z = "Please enter Username and Password";
            else
                try {
                    String value = pref.getString("code", "0");
                    if (value.equals(password)) {
                        //fazer dois rounds de pbkdf2 , o primeiro round é privado
                        pref.edit().putString("www.uminho.pt", "{'username':'admin, 'password':'admin'}").commit();
                        z = "Login successful";
                        isSuccess = true;
                    } else {
                        z = "Invalid Username!";
                        isSuccess = false;
                    }

                } catch (Exception ex) {
                    isSuccess = false;

                    z = ex.getMessage();

                }
            return z;
        }
    }

    public class json2List extends Thread {
        Boolean isSuccess = false;
        List<Account> accounts;
        JSONObject jobj;
        String z = "";

        public json2List(JSONObject r)
        {
            this.accounts = new ArrayList<Account>();
            this.jobj = r;
        }

        public List getList(){
            return this.accounts;
        }


        public void run() {
            try {
                JSONArray recs = jobj.getJSONArray("acc");
                Log.d("TAMANHO DO ARRAY", "This is my log message at the debug level here: "+ recs.length());

                for (int i = 0; i < recs.length(); ++i) {
                    JSONObject rec = recs.getJSONObject(i);
                    String website  = rec.getString("website");
                    String tag      = rec.getString("website");
                    String username = rec.getString("website");
                    String password = rec.getString("website");
                    this.accounts.add(new Account(website, tag, username, password));
                    Log.d("TAMANHO DO ACCOUNTS", "This is my log message at the debug level here: "+ accounts.size());

                }


            } catch (Exception ex) {
                Log.d("create",ex.getMessage());
                z = ex.getMessage();
            }
        }
    }
}

