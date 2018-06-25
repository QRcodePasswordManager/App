package uminho.qrcode;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class checkCredenciais extends AppCompatActivity {

    TextView val;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_credenciais);
        val = (TextView)findViewById(R.id.valores);
        val.setText("Dominio.\tUsername\\Email.\tPassword\n\r");
        readFromFile();
        configureVoltarButton();

    }

    private void configureVoltarButton(){
        Button voltar = (Button)findViewById(R.id.voltar);
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void readFromFile() {

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //Request permission
            ActivityCompat.requestPermissions(checkCredenciais.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
            return;
        }

        String line;

        File file = new File(Environment.getExternalStorageDirectory() +
                File.separator + "/qrcode/credenciais.txt");

        if (file.exists()) {
            try {
                BufferedReader input = new BufferedReader(new FileReader(file));
                if (!input.ready()) {
                    throw new IOException();
                }
                while ((line = input.readLine()) != null) {
                    val.append(line+"\n\r");
                }
                input.close();

            } catch (IOException e) {

            }
        }
        else{
            val.setText("File não existe");
        }

        return;
    }

}
