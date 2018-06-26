package uminho.qrcode;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.os.Vibrator;
import android.provider.DocumentsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.security.cert.CertificateFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class QRCode extends AppCompatActivity {

    SurfaceView cameraPreview;
    TextView txtResult;
    BarcodeDetector barcodeDetector;
    CameraSource cameraSource;
    final int RequestCameraPermissionID = 1001;
    RequestQueue queue;
    String last = "";

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCameraPermissionID: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }
                    try {
                        cameraSource.start(cameraPreview.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qrcode);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        Network network = new BasicNetwork(new HurlStack());
        queue = new RequestQueue(cache, network);
        queue.start();

        configureVoltarButton();

        cameraPreview = (SurfaceView) findViewById(R.id.cameraPreview);



        txtResult = (TextView) findViewById(R.id.txtResult);

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();



        cameraSource = new CameraSource.
                Builder(this, barcodeDetector)
                .setRequestedPreviewSize((int)(width*0.8),(int)(height*0.8))
                .build();

        cameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    //Request permission
                    ActivityCompat.requestPermissions(QRCode.this,
                            new String[]{Manifest.permission.CAMERA}, RequestCameraPermissionID);
                    return;
                }
                try {
                    cameraSource.start(cameraPreview.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrcodes = detections.getDetectedItems();
                if(qrcodes.size() != 0){
                    txtResult.post(new Runnable() {
                        @Override
                        public void run() {
                            ConnectivityManager connectivityManager
                                    = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                            if(activeNetworkInfo != null && activeNetworkInfo.isConnected()) {

                                String now = qrcodes.valueAt(0).displayValue;
                                if (!last.equals(now)) {

                                    //Create vibrate
                                    Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                                    vibrator.vibrate(1000);
                                    last = qrcodes.valueAt(0).displayValue;
                                    httpget(last);
                                    //
                                }

                            }
                            else{
                                txtResult.setText("No Network Access");
                            }
                        }
                    });
                }
            }
        });
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

    private void httpget(final String id) {

        Thread sendHttpRequestThread = new Thread() {
            @Override
            public void run() {
                String url = "http://10.0.2.2:8081/session/"+id;

                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    if(response==null && response.equals("")){
                                        txtResult.append("ID inválido");
                                    }
                                    JSONObject testV = new JSONObject(response);
                                    String key = testV.getString("client_key");
                                    httppost(key, id);
                                } catch (JSONException e) {
                                    txtResult.append("Dados recebidos não são JSON");
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                txtResult.append("Erro a aceder à página");
                            }
                        });

                queue.add(stringRequest);
            }
        };
        sendHttpRequestThread.start();

    }

    private void httppost(final String key, final String id) {

        Thread sendHttpResponseThread = new Thread() {
            @Override
            public void run() {
                String url = "http://10.0.2.2:8081/session/"+id;
                JSONObject jsonBody = new JSONObject();
                try {
                    jsonBody.put("hidden_user", RSAEncrypt(key, "user"));
                    jsonBody.put("hidden_password", RSAEncrypt(key, "Encrypt"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                final String requestBody = jsonBody.toString();




                StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response) {
                                // response
                                txtResult.append("ola" + response.toString());
                                Log.d("Response", response.toString());
                            }
                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("ERROR","error => "+error.toString());
                            }
                        }
                ) {
                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        try {
                            return requestBody == null ? null : requestBody.getBytes("utf-8");
                        } catch (UnsupportedEncodingException uee) {
                            Log.d("Encoding", uee.toString());
                            return null;
                        }
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String>  params = new HashMap<String, String>();
                        params.put("Content-Type", "application/json");
                        Log.d("headers", "header");

                        return params;
                    }

                    @Override
                    protected Response<String> parseNetworkResponse(NetworkResponse response) {
                        String responseString = "";
                        if (response != null) {
                            responseString = String.valueOf(response.statusCode);
                            // can get more details such as response.headers
                        }
                        Log.d("ResponseNetwork", "ola");
                        return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                    }
                };

                queue.add(postRequest);
            }
        };
        sendHttpResponseThread.start();

    }

    public String RSAEncrypt (String key, final String plain)
    {
        Cipher cipher = null;
        BufferedReader pemReader = null;
        try {
            pemReader = new BufferedReader(new InputStreamReader(
                        new ByteArrayInputStream(key.getBytes("UTF-8"))));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        StringBuffer content = new StringBuffer();
        String line = null;
        try {
            while ((line = pemReader.readLine()) != null) {
                if (line.indexOf("-----BEGIN PUBLIC KEY-----") != -1) {
                    while ((line = pemReader.readLine()) != null) {
                        if (line.indexOf("-----END PUBLIC KEY") != -1) {
                            break;
                        }
                        content.append(line.trim());
                    }
                    break;
                }
            }
        }catch(IOException e){

        }

        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            Log.d("algorith", "Error");
        }
        PublicKey publickey = null;
        try {
            publickey = keyFactory.generatePublic(new X509EncodedKeySpec(Base64.decode(content.toString(), Base64.DEFAULT)));
        } catch (InvalidKeySpecException e) {
            Log.d("Key", "Error");
        }

        try {
            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC");
        } catch (NoSuchAlgorithmException e) {
            Log.d("Algorithm", "Error");
        } catch (NoSuchProviderException e) {
            Log.d("Provider", "Error");
        } catch (NoSuchPaddingException e) {
            Log.d("Padding", "Error");
        }
        try {
            cipher.init(Cipher.ENCRYPT_MODE, publickey);
        } catch (InvalidKeyException e) {
            Log.d("Key2", "Error");
        }
        String dec = null;
        try {
            dec = Base64.encodeToString(cipher.doFinal(plain.getBytes()), Base64.DEFAULT);
        } catch (IllegalBlockSizeException e) {
            Log.d("Block", "Error");
        } catch (BadPaddingException e) {
            Log.d("Padding", "Error");
        }
        Log.d("keykeykey", dec);
        return dec;

    }


}
