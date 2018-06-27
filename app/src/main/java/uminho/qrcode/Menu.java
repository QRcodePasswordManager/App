package uminho.qrcode;

import android.accounts.Account;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
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

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


public class Menu extends Activity {
    String table;
    private ImageButton addwebsite, camera;
    private TextView warning;
    private JSONObject temp;
    private MyAdapter adapter;
    private ListView listView;
    private Map<String, String> acc;
    private SharedPreferences pref;
    RequestQueue queue;
    private JSONObject config;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listviews);

        addwebsite  = (ImageButton) findViewById(R.id.imageButton4);
        camera      = (ImageButton) findViewById(R.id.imageButton9);
        warning = (TextView) findViewById(R.id.warning);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        acc = (Map<String, String>)bundle.getSerializable("accounts");
        try {
            config = new JSONObject(acc.get("config"));
            acc.remove("config");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        Network network = new BasicNetwork(new HurlStack());
        queue = new RequestQueue(cache, network);
        queue.start();


        addwebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertRecord();
            }
        });

        final Intent inte = new Intent(Menu.this, QRCode.class);


        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(inte, 111);
            }
        });

        adapter = new MyAdapter(acc);
        listView = (ListView) findViewById(R.id.restaurantlview);

        listView.setAdapter(adapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View v, int pos, long id) {
                ImageButton del     = (ImageButton) v.findViewById(R.id.deletebutton);
                ImageButton edit    = (ImageButton) v.findViewById(R.id.editbutton);
                TextView key          = (TextView) v.findViewById(R.id.item_title);
                del.setVisibility(View.VISIBLE);
                edit.setVisibility(View.VISIBLE);
                del.setOnClickListener(new MyListener(v, key.getText().toString(), acc.get(key.getText().toString()), 0));
                edit.setOnClickListener(new MyListener(v, key.getText().toString(), acc.get(key.getText().toString()), 1));

                return true;
            }
        });


        // ListView Item Click Listener

    }
    public class MyAdapter extends BaseAdapter {
        private ArrayList mData;

        public MyAdapter(Map<String, String> map) {
            mData = new ArrayList();
            mData.addAll(map.entrySet());
        }

        public void refreshAdapter(Map<String, String> map){
            mData = new ArrayList();
            mData.addAll(map.entrySet());
            this.notifyDataSetChanged();
        }




        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Map.Entry<String, String> getItem(int position) {
            return (Map.Entry) mData.get(position);
        }

        @Override
        public long getItemId(int position) {

            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final View result;

            if (convertView == null) {
                result = getLayoutInflater().inflate(R.layout.item_layout, parent, false);
            } else {
                result = convertView;
            }

            Map.Entry<String, String> item = getItem(position);

            ImageButton del     = (ImageButton)result.findViewById(R.id.deletebutton);
            ImageButton edit    = (ImageButton) result.findViewById(R.id.editbutton);
            TextView text1 = (TextView) result.findViewById(R.id.item_title);
            text1.setText(item.getKey());
            TextView text2 = (TextView) result.findViewById(R.id.item_desc);
            TextView text3 = (TextView) result.findViewById(R.id.item_lastmod);
            del.setVisibility(View.INVISIBLE);
            edit.setVisibility(View.INVISIBLE);
            try {
                temp = new JSONObject(item.getValue());
                text2.setText(temp.getString("username"));
                text3.setText(temp.getString("lastmod"));

            } catch (JSONException e) {
                e.printStackTrace();
            }


            return result;
        }



    }

    private class MyListener implements View.OnClickListener{
        private View v;
        private String key;
        private String res;
        private int code;

        public MyListener(View context, String key, String res, int code){
            v           = context;
            this.key    = key;
            this.res    = res;
            this.code   = code;
        }

        @Override
        public void onClick(View v){
            if(code==0) {
                acc.remove(key);
            }else{
                Log.d("editablerecord1", res);
                editRecord(key, res);
            }

            adapter.refreshAdapter(acc);
        }
    }

    public void editRecord(String key, String res){
        Intent intent = new Intent(this, NewEntry.class);
        intent.putExtra("data", res);
        startActivityForResult(intent, 98);
    }
    public void insertRecord(){
        Intent intent = new Intent(this, NewEntry.class);
        startActivityForResult(intent, 99);
    }

    protected void onResume(Bundle savedInstanceState) {
        listView.setAdapter(adapter);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        pref = getApplicationContext().getSharedPreferences("file009", MODE_PRIVATE);
        if (requestCode == 99) {
            if(resultCode == Activity.RESULT_OK){
                String result = data.getStringExtra("result");
                try {
                    JSONObject tmp = new JSONObject(result);
                    tmp.put("password", Cripto.encrypt(config.getString("mk"), tmp.getString("iv"), tmp.getString("password")));
                    acc.put(tmp.getString("website"), tmp.toString());
                    pref.edit().putString(tmp.getString("website"),tmp.toString()).commit();
                    adapter.refreshAdapter(acc);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //nada a fazer
            }
        }else if(requestCode==98){
            if(resultCode == Activity.RESULT_OK){
                String result = data.getStringExtra("result");
                try {
                    JSONObject tmp = new JSONObject(result);
                    tmp.put("password", Cripto.encrypt(config.getString("mk"), tmp.getString("iv"), tmp.getString("password")));
                    acc.remove(tmp.getString("website"));
                    pref.edit().remove(tmp.getString("website").toString()).commit();
                    //acc.put(tmp.getString("website"), tmp.toString());
                    adapter.refreshAdapter(acc);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //nada a fazer
            }
        }
        else if (requestCode == 111) {

            if (resultCode ==  Activity.RESULT_OK) {
                Log.d("TAG!", "OLAAAAAA");
                String dom = data.getStringExtra("dominio");
                String key = data.getStringExtra("key");
                String id = data.getStringExtra("id");
                Log.d("TAG!", "OLAAAAAA2");
                httppost(key, id, dom);
            } else if(resultCode ==  2){
                warning.setText("Sessão não iniciada");
            } else if(resultCode == 3){
                warning.setText("Error Volley");
            }
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        //isto nao é garantido que corra, adicionar ao ficheiro sempre que possivel
        SharedPreferences pref = getApplicationContext().getSharedPreferences("qrfilek", MODE_PRIVATE);
        for(Map.Entry<String,?> entry : acc.entrySet()){
            JSONObject rec = null;
            try {
                rec = new JSONObject(entry.getValue().toString());
                pref.edit().putString(rec.getString("website"), rec.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.d("onDestroy", "aqui estamos");
    }

    private void httppost(final String key, final String id, final String dom) {

        Thread sendHttpResponseThread = new Thread() {
            @Override
            public void run() {
                String url = "http://10.0.2.2:8081/session/"+id;
                JSONObject jsonBody = new JSONObject();
                try {
                    if(acc.containsKey(dom) != false){
                        JSONObject aux = new JSONObject(acc.get(dom));
                        String user = aux.getString("username");
                        String pass = Cripto.decrypt(config.getString("mk"), aux.getString("iv"), aux.getString("password"));
                        Log.d("Teste", key);
                        jsonBody.put("hidden_user", RSAEncrypt(key, user));
                        jsonBody.put("hidden_password", RSAEncrypt(key, pass));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(acc.containsKey(dom) != false) {
                    final String requestBody = jsonBody.toString();

                    StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    // response
                                    if(response.equals("200")){
                                        warning.setText("Success");
                                    }
                                    else{
                                        warning.setText("Conectivity Failed");
                                    }

                                    Log.d("Response", response.toString());
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d("ERROR", "error => " + error.toString());
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
                            Map<String, String> params = new HashMap<String, String>();
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
                else {
                    warning.setText("Credenciais Inexistentes");
                }

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


