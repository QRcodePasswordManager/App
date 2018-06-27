package pt.eatbit.qrauth;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
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
    private boolean first, resume = false;
    private int count = 0;
    private SharedPreferences pref;
    private JSONObject response = null;
    private String jsonPacket;
    private List accounts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getApplicationContext().getSharedPreferences("file009", MODE_PRIVATE);
        if(pref.getString("setup", "0").equals("0")){
            Intent intent = new Intent(this, Register.class);
            startActivityForResult(intent, 11);
        }else{
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
        }

    }


    @Override
    protected void onResume(){
        super.onResume();
        count++;
        Log.d("count", "count"+count+" "+first);

        if(count == 2 && first){
            Log.d("count", "coun"+count);
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
        }else if(count > 2){
            finish();
        }else if(count == 2 && !first ){
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 11) {
            if(resultCode == Activity.RESULT_OK){
                String result = data.getStringExtra("first");
                Log.d("Stringz", result);
                first = result.equals("true");
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //nada a fazer
            }
        }
    }
}
