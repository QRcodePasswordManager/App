package pt.eatbit.qrauth;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class Menu extends Activity {
    String table, query;
    private ImageButton addwebsite, camera;
    private JSONObject temp;
    private MyAdapter adapter;
    private ListView listView;
    private Map<String, String> acc;
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

        addwebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertRecord();
            }
        });

        adapter = new MyAdapter(acc);
        listView = (ListView) findViewById(R.id.restaurantlview);

        listView.setAdapter(adapter);


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

            if (position == 0) {
                result.setVisibility(View.GONE);
                return result;
            }

            Map.Entry<String, String> item = getItem(position);

            TextView text1 = (TextView) result.findViewById(R.id.item_title);
            text1.setText(item.getKey());
            TextView text2 = (TextView) result.findViewById(R.id.item_desc);
            TextView text3 = (TextView) result.findViewById(R.id.item_lastmod);

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
                try {
                    Log.d("MENU RESULT ADDED", result);
                    JSONObject tmp = new JSONObject(result);
                    JSONObject tmp2 = new JSONObject(acc.get("config"));
                    tmp.put("password", Cripto.encrypt(tmp2.getString("mk"), tmp.getString("iv"), tmp.getString("password")));
                    acc.put(tmp.getString("website"), tmp.toString());
                    Log.d("COCOCOCOCOCOCOCOCOCOCOC", acc.toString());
                    adapter.refreshAdapter(acc);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }
}
