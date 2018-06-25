package uminho.qrcode;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);

        configureQRCode();
        configureCredenciais();
    }

    private void configureQRCode(){
        Button qrcode = (Button)findViewById(R.id.qrcode);
        qrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainPage.this, QRCode.class));
            }
        });
    }

    private void configureCredenciais(){
        Button credenciais = (Button)findViewById(R.id.credenciais);
        credenciais.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainPage.this, Credenciais.class));
            }
        });
    }
}
