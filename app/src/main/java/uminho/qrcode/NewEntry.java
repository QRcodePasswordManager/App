package uminho.qrcode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by sousa on 26-06-2018.
 */

public class NewEntry extends AppCompatActivity {
    private String result;
    private EditText website, username, password;
    private Button confirm, cancel;
    private JSONObject tmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newentry);
        Intent returnIntent = new Intent();
        website     = (EditText) findViewById(R.id.websitet);
        username    = (EditText) findViewById(R.id.usernamet);
        password    = (EditText) findViewById(R.id.usernamet);
        confirm     = (Button) findViewById(R.id.confirm);
        cancel      = (Button) findViewById(R.id.cancel);

        tmp = new JSONObject();



        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                try {
                    tmp.put("website", website.getText().toString());
                    tmp.put("username", username.getText().toString());
                    tmp.put("password", password.getText().toString());
                    tmp.put("put", "1");
                    tmp.put("iv",Cripto.toHex(Cripto.randomSalt()));

                    tmp.put("lastmod", (new SimpleDateFormat("dd-MM-yyyy")).format(new Date()));
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                returnIntent.putExtra("result",tmp.toString());
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result",tmp.toString());
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });
    }
}