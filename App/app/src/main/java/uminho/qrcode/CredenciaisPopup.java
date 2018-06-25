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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class CredenciaisPopup extends AppCompatActivity {
    String webs;
    TextView warning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.credenciais_popup);
        webs = getIntent().getStringExtra("website");

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        warning = (TextView)findViewById(R.id.warning);

        getWindow().setLayout((int) (width), (int)(height));
        configureSimButton();
        configureNaoButton();
    }

    public void removeLine(String website) throws IOException {
        File file = new File(Environment.getExternalStorageDirectory() +
                File.separator + "/qrcode/credenciais.txt");
        final List<String> lines = new LinkedList<>();
        final Scanner reader = new Scanner(new FileInputStream(file), "UTF-8");
        while(reader.hasNextLine()) {
            try {
                JSONObject testV = new JSONObject(reader.nextLine());
                String web = testV.optString("website");
                if(!web.equals(website)) {
                    lines.add(web);
                }
            } catch (JSONException e) {
                warning.append("ErroNovo");
            }
        }
        reader.close();
        final BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
        for(final String line : lines)
            writer.write(line);
        writer.flush();
        writer.close();
        return;
    }

    public void configureSimButton(){
        final TextView warning = (TextView)findViewById(R.id.warning);
        final Button sim = (Button)findViewById(R.id.sim);
        sim.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                try {
                    removeLine(webs);
                    setResult(1);
                    finish();
                } catch (IOException e) {
                    warning.append("ErroVelho\n\r");
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
