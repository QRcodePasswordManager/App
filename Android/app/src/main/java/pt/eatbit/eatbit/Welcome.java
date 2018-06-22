package pt.eatbit.eatbit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by sousa on 09-06-2017.
 */


public class Welcome extends AppCompatActivity {
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
        super.onCreate(savedInstanceState);
        pref = getApplicationContext().getSharedPreferences("prefinfo", MODE_PRIVATE);
        if(pref.getString("setup", "0").equals("0")){
            Intent intent = new Intent(this, Register.class);
            startActivity(intent);
        }else{
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
        }

    }

    @Override
    protected void onResume(){
        super.onResume();
        finish();
    }
}
