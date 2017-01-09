package com.example.liuziwen.mystatckview;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

public class FrameActivity extends Activity {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame);
        getIntent().getAction();
        final ImageView iv = (ImageView) findViewById(R.id.iv);
        final View view = getWindow().getDecorView();
        float a = getWindow().getAttributes().alpha;
        float b = getWindow().getAttributes().dimAmount;

        Log.d("intent action = ", getCallingActivity() + "=" + " " + a + " " + b);
        iv.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                iv.getViewTreeObserver().removeOnPreDrawListener(this);
                Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);

                getWindow().getDecorView().draw(canvas);
                iv.setImageBitmap(bitmap);
                return true;
            }
        });
        getCallingActivity();

    }
}
