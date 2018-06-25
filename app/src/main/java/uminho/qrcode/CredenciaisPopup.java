package uminho.qrcode;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class CredenciaisPopup extends AppCompatActivity {
    String webs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.credenciais_popup);
        webs = getIntent().getStringExtra("website");

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width), (int)(height));
        configureSimButton();
        configureNaoButton();
    }

    public void removeLine(String website) throws IOException {
        File file = new File(Environment.getExternalStorageDirectory() +
                File.separator + "/qrcode/credenciais.txt");
        final List<String> lines = new LinkedList<>();
        BufferedReader input = new BufferedReader(new FileReader(file));
        String line;
        while((line = input.readLine()) != null) {
            try {
                JSONObject testV = new JSONObject(line);
                String web = testV.optString("website");
                if(!web.equals(website)) {
                    lines.add(testV.toString());
                }
            } catch (JSONException e) {
            }
        }
        input.close();
        final BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
        for(String l : lines)
            writer.write(l);
        writer.flush();
        writer.close();
        return;
    }

    public void configureSimButton(){
        final Button sim = (Button)findViewById(R.id.sim);
        sim.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                try {
                    removeLine(webs);
                    setResult(1);
                    finish();
                } catch (IOException e) {
                }
            }
        });
    }

    public void configureNaoButton(){
        Button nao = (Button)findViewById(R.id.nao);
        nao.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                setResult(0);
                finish();
            }
        });
    }

}
