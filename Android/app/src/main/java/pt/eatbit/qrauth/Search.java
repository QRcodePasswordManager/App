package pt.eatbit.qrauth;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import pt.eatbit.eatbit.R;

import static android.view.DragEvent.ACTION_DROP;

public class Search extends AppCompatActivity implements View.OnDragListener, View.OnTouchListener{
    ProgressBar pb;
    ImageView ohbutton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_search);

        pb = (ProgressBar) findViewById(R.id.progressBar3);

        pb.setVisibility(View.INVISIBLE);
        ohbutton      = (ImageView) findViewById(R.id.button);
        ImageView fork          = (ImageView) findViewById(R.id.fork);

        ohbutton.setOnTouchListener(this);
        ohbutton.setOnDragListener(this);
        fork.setOnDragListener(this);

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (v.getId() != R.id.button) return false;

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
            v.startDrag(null, shadowBuilder, v, 0);

            return true;
        }
        return false;
    }

    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
        pb.setVisibility(View.INVISIBLE);
        ohbutton.setVisibility(View.VISIBLE);

    }
    @Override
    public boolean onDrag(View v, DragEvent event) {

        int action = event.getAction();
        switch (action) {

            case DragEvent.ACTION_DRAG_STARTED:
                if(v.getId() == R.id.button)
                    v.setVisibility(View.INVISIBLE);
                return true;

            case DragEvent.ACTION_DRAG_ENDED:
                if (v.getId() == R.id.button)
                    v.setVisibility(View.VISIBLE);
                return true;

            case ACTION_DROP: {
                if (v.getId() == R.id.button){
                    v.setVisibility(View.VISIBLE);
                    return false;
                }
                pb.setVisibility(View.VISIBLE);
                ohbutton.setVisibility(View.INVISIBLE);
                Intent searchIntent = new Intent(this, Menu.class);
                searchIntent.putExtra("table","first");
                searchIntent.putExtra("string","second");
                startActivity(searchIntent);

            }
        }
        return true;
    }
}


