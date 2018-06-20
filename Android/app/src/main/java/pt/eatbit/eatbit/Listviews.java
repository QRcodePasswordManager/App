package pt.eatbit.eatbit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Listviews extends Activity {
    ListView listView;
    String table, query;
    public List<Restaurant> restaurants;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listviews);
        Intent myIntent = getIntent();
        String table = myIntent.getStringExtra("first");
        String query = myIntent.getStringExtra("second");

        // Get ListView object from xml



        // Defined Array values to show in ListView
        restaurants = new ArrayList<Restaurant>();
        List<Dish> dishes;

        Thread querying = new Thread(new newquery(restaurants));

        querying.start();

        try {
            querying.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



        ArrayAdapter<Restaurant> adapter = new ListAdapter();
        ListView listView = (ListView) findViewById(R.id.restaurantlview);

        // Assign adapter to ListView
        listView.setAdapter(adapter);

        // ListView Item Click Listener

    }

    public class ListAdapter extends ArrayAdapter<Restaurant>{
        public ListAdapter(){
            super(Listviews.this, R.layout.item_layout, restaurants);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            View itemView = convertView;
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.item_layout, parent, false);
            }

            Restaurant curr = restaurants.get(position);

            /*
            ImageView imageview = (ImageView) itemView.findViewById(R.id.item_picture);
            imageview.setImageResource();
            */

            TextView text1 = (TextView) itemView.findViewById(R.id.item_title);
            text1.setText(curr.getName());

            TextView text2 = (TextView) itemView.findViewById(R.id.item_desc);
            text2.setText(curr.getPhoto());

            TextView text3 = (TextView) itemView.findViewById(R.id.item_rating);
            text3.setText(Float.toString(curr.getRating()));

            ImageView img = (ImageView) itemView.findViewById(R.id.item_picture);
            URL newurl = null;
            try {
                newurl = new URL(curr.getPhoto());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            Bitmap mIcon_val = null;
            try {
                mIcon_val = BitmapFactory.decodeStream(newurl.openConnection() .getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            img.setImageBitmap(mIcon_val);






            return itemView;
            //return super.getView(position, convertView, parent);
        }

    }

    public class newquery extends Thread {
        String z = "";
        Boolean isSuccess = false;
        List<Restaurant> restaurants;

        public newquery(List<Restaurant> r)
        {
            this.restaurants = r;
        }

        public void run() {
            //Code


            try {
                MSConnect connection = new MSConnect();
                Connection con = connection.connect();

                if (con == null) {
                    z = "Check Your Internet Access!";
                } else {
                    String query = "SELECT TOP 15 * FROM Restaurante;";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    if(rs.getFetchSize()>0){

                        while(rs.next()){
                            Restaurant rest = new Restaurant();
                            rest.setId(rs.getInt(1));
                            rest.setName(rs.getString(2));
                            rest.rating(rs.getFloat(3));
                            rest.hours(rs.getString(4));
                            rest.phonenumber(rs.getString(5));
                            rest.coord(rs.getString(7));
                            rest.type(rs.getString(8));
                            rest.addr(rs.getString(9));
                            rest.image(rs.getString(10));
                            restaurants.add(rest);
                        }

                        z = "Registered successfully";
                        isSuccess = true;
                        con.close();
                    } else {
                        z = "Invalid Credentials!";
                        isSuccess = false;
                    }
                }
            } catch (Exception ex) {
                isSuccess = false;
                z = ex.getMessage();
            }
        }
    }
}
