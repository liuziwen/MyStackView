package com.example.liuziwen.mystatckview;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MyAdapterView stackView;
    int i = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        stackView = (MyAdapterView) findViewById(R.id.stackView);
        List<String> titles = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            titles.add("title " + i);
        }
        stackView.setTitles(titles);
        stackView.setBaseAdapter(new StackViewAdapter());
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.stack_view_item, null, false);
        final LinearLayout ll = (LinearLayout) findViewById(R.id.ll);
        Button bn = (Button) view.findViewById(R.id.stack_item_bn);

        bn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectAnimator.ofFloat(ll, "translationY", ll.getTranslationY(), ll.getTranslationY() + i * 50).setDuration(1000).start();
                i = i * -1;
                Log.d("MainActivity", "translationY = " + ll.getTranslationY());
            }
        });
        ll.addView(view);

    }

    public class StackViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 30;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.stack_view_item, parent, false);
            Button bn = (Button) view.findViewById(R.id.stack_item_bn);
            bn.setText("" + position);
            bn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this, "position", Toast.LENGTH_SHORT).show();
                    MainActivity.this.startActivity(new Intent(MainActivity.this, FrameActivity.class));
                }
            });
            return view;
        }
    }

    public void onStop() {
        super.onStop();
        Log.d("blur", "====onStop");
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d("blur", "====onDestroy");
    }

    public void onPause() {
        super.onPause();
        Log.d("blur", "====onPause");
    }
}
