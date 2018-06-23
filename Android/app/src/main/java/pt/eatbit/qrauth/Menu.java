package pt.eatbit.qrauth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class Menu extends Activity {
    ListView listView;
    String table, query;
    public List<Account> accounts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listviews);
        Intent myIntent = getIntent();

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        Map<String, String> acc = (Map<String, String>)bundle.getSerializable("accounts");



        MyAdapter adapter = new MyAdapter(acc);
        ListView listView = (ListView) findViewById(R.id.restaurantlview);

        listView.setAdapter(adapter);

        // ListView Item Click Listener

    }
    public class MyAdapter extends BaseAdapter {
        private final ArrayList mData;

        public MyAdapter(Map<String, String> map) {
            mData = new ArrayList();
            mData.addAll(map.entrySet());
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

            if(position == 0){
                result.setVisibility(View.GONE);
                return result;
            }

            Map.Entry<String, String> item = getItem(position);

            TextView text1 = (TextView) result.findViewById(R.id.item_title);
            text1.setText(item.getKey());
            TextView text2 = (TextView) result.findViewById(R.id.item_desc);
            text2.setText(item.getValue());

            return result;
        }
    }
}
