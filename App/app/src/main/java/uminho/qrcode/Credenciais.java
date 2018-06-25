package uminho.qrcode;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Credenciais extends AppCompatActivity {

    Button guardar, teste, voltar;
    EditText password, confirmacao, username, website;
    TextView warning;
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.credenciais);

        guardar = (Button)findViewById(R.id.guardar);
        voltar = (Button)findViewById(R.id.voltar);
        teste = (Button)findViewById(R.id.teste);
        password = (EditText)findViewById(R.id.password);
        confirmacao = (EditText)findViewById(R.id.confirmacao);
        username = (EditText)findViewById(R.id.username);
        website = (EditText)findViewById(R.id.website);
        warning = (TextView)findViewById(R.id.warning);

        configureGuardarButton();
        configureTesteButton();
        configureVoltarButton();
    }

    private void configureGuardarButton(){
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pass = password.getText().toString();
                String conf = confirmacao.getText().toString();

                if(!pass.equals(conf)){
                    warning.setText("Passwords não coincidem");
                }
                else{
                    int res = 0;
                    String web = website.getText().toString();
                    if(existeWebsite(web)) {
                        Intent intent = new Intent(Credenciais.this, CredenciaisPopup.class);
                        intent.putExtra("website", web);
                        startActivityForResult(intent, 1);
                    }
                    else{
                        JSONObject json = new JSONObject();
                        try {
                            String user = username.getText().toString();
                            json.put("website", web);
                            json.put("username", user);
                            json.put("password", pass);
                            writeToFile(json.toString());

                        } catch (JSONException e) {
                            warning.setText("Erro\n\r");
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 1){
            String user = username.getText().toString();
            String web = website.getText().toString();
            String pass = password.getText().toString();
            JSONObject json = new JSONObject();
            try {
                json.put("website", web);
                json.put("username", user);
                json.put("password", pass);
                writeToFile(json.toString());

            } catch (JSONException e) {
                warning.append("Erro\n");
            }
        }
    }

    private boolean existeWebsite(String website){
        File file = new File(Environment.getExternalStorageDirectory() +
                File.separator + "/qrcode/credenciais.txt");
        final List<String> lines = new LinkedList<>();
        final Scanner reader;
        try {
            reader = new Scanner(new FileInputStream(file), "UTF-8");
            while(reader.hasNextLine()) {
                try {
                    JSONObject testV = new JSONObject(reader.nextLine());
                    String web = (String)testV.get("website");
                    if(web.equals(website))
                        return true;
                } catch (JSONException e) {
                    warning.append("Erros\n\r");
                }
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void configureTesteButton() {
        teste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Credenciais.this, checkCredenciais.class));
            }
        });
    }

    private void configureVoltarButton(){
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void writeToFile(String data) {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //Request permission
            ActivityCompat.requestPermissions(Credenciais.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
            return;
        }

        File folder = new File(Environment.getExternalStorageDirectory() +
                File.separator + "/qrcode");
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdirs();
        }
        file = new File(Environment.getExternalStorageDirectory() +
                File.separator + "/qrcode/credenciais.txt");
        try{
            OutputStreamWriter file_writer = new OutputStreamWriter(new FileOutputStream(file,true));
            BufferedWriter buffered_writer = new BufferedWriter(file_writer);
            buffered_writer.write(data+"\n");
            buffered_writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
