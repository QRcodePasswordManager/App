package pt.eatbit.qrauth;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.view.View.GONE;


public class Menu extends Activity {
    String table, query;
    private ImageButton addwebsite, camera;
    private JSONObject temp;
    private MyAdapter adapter;
    private ListView listView;
    private Map<String, String> acc;
    private JSONObject config;
    public List<Account> accounts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listviews);

        addwebsite  = (ImageButton) findViewById(R.id.imageButton4);
        camera      = (ImageButton) findViewById(R.id.imageButton3);

        Intent myIntent = getIntent();
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        acc = (Map<String, String>)bundle.getSerializable("accounts");
        try {
            config = new JSONObject(acc.get("config"));
            acc.remove("config");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        addwebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertRecord();
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
        if (requestCode == 99) {
            if(resultCode == Activity.RESULT_OK){
                String result = data.getStringExtra("result");
                Log.d("editrr", result);
                try {
                    JSONObject tmp = new JSONObject(result);
                    tmp.put("password", Cripto.encrypt(config.getString("mk"), tmp.getString("iv"), tmp.getString("password")));
                    acc.put(tmp.getString("website"), tmp.toString());
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
                    //acc.remove(tmp.getString("website"));
                    acc.put(tmp.getString("website"), tmp.toString());
                    adapter.refreshAdapter(acc);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //nada a fazer
            }
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        //isto nao é garantido que corra, adicionar ao ficheiro sempre que possivel
        SharedPreferences pref = getApplicationContext().getSharedPreferences("qrfile", MODE_PRIVATE);
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
}
