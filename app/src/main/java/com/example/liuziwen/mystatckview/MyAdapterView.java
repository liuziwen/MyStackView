package com.example.liuziwen.mystatckview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by liuziwen on 16/10/1.
 */
public class MyAdapterView extends ViewGroup implements View.OnClickListener {

    private final int ITEM_TITLE_HEIGHT = dpToPx(50);
    private final int ITEM_CONTENT_HEIGHT = dpToPx(200);
    private int openId = 0, lastId = 0;
    private int childCount = 0;
    private int contentViews = 2;

    private int left, top, right, bottom;

    private BaseAdapter baseAdapter;
    private LinearLayout last, current, next;
    private View nextContent;
    private List<String> titles = new ArrayList<>();

    private AnimatorSet set;


    public MyAdapterView(Context context) {
        super(context);
        init();
    }

    public MyAdapterView(Context context, List<String> titles, BaseAdapter baseAdapter) {
        super(context);
        this.titles = titles;
        this.baseAdapter = baseAdapter;
        init();
    }

    public MyAdapterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d("MyAdapterView", "MyAdapterView(Context context, AttributeSet attrs)");
        init();
    }

    public MyAdapterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setBaseAdapter(BaseAdapter baseAdapter) {
        this.baseAdapter = baseAdapter;
        current.addView(baseAdapter.getView(openId, null, current));
    }

    public void setTitles(List<String> titles) {
        this.titles = titles;
        for (int i = 0; i < titles.size(); i++) {
            View v = getTitleView(titles.get(i));
            addView(v);
        }
        addView(current);
        addView(next);

    }

    private void init() {
        //openId = getChildCount()-1;
        current = getItemContentView();
        next = getItemContentView();
        Log.d("MyAdapterView", "openid = " + openId);
        setPadding(0, 0, 0, 0);
    }

    private View getTitleView(String title) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.stack_view_title_view, this, false);
        TextView tv = (TextView) v.findViewById(R.id.title);
        tv.setText(title);
        return v;
    }

    private LinearLayout getItemContentView() {
        LinearLayout view = new LinearLayout(getContext());
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, ITEM_CONTENT_HEIGHT);
        view.setLayoutParams(lp);
        view.setGravity(Gravity.CENTER);
        view.setOrientation(LinearLayout.VERTICAL);
        view.setBackgroundColor(Color.BLUE);
        return view;
    }

    @Override
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.d("MyAdapterView", "onlayout  = " + getPaddingLeft() + " " + getPaddingTop() + " " + getPaddingRight() + " " + getPaddingBottom());

        l = 0;
        left = l;
        top = t;
        right = r;
        bottom = b;
        int count = getChildCount() - contentViews;

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (i < 0) {
                child.layout(0, i * ITEM_TITLE_HEIGHT, r - l, (i + 1) * ITEM_TITLE_HEIGHT);
            } else if (i == 0) {
                child.layout(0, i * ITEM_TITLE_HEIGHT, r - l, (i + 1) * ITEM_TITLE_HEIGHT);
            } else {
                child.layout(0, i * ITEM_TITLE_HEIGHT + ITEM_CONTENT_HEIGHT, r - l, (i + 1) * ITEM_TITLE_HEIGHT + ITEM_CONTENT_HEIGHT);
            }
        }
        current.layout(0, (openId + 1) * ITEM_TITLE_HEIGHT, r - l, (openId + 1) * ITEM_TITLE_HEIGHT + ITEM_CONTENT_HEIGHT);

    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height, width;
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode == MeasureSpec.EXACTLY) {
            height = MeasureSpec.getSize(heightMeasureSpec);
        } else {
            height = (getChildCount() - contentViews) * ITEM_TITLE_HEIGHT + ITEM_CONTENT_HEIGHT;
        }
        width = MeasureSpec.getSize(widthMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    public int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getContext().getResources().getDisplayMetrics());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("My", "onTouchEvent");
        if (set == null || !set.isRunning()) {
            int down_x = 0, down_y = 0;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    down_x = (int) event.getX();
                    down_y = (int) event.getY();
                    Log.d("My down", down_x + " " + (int) event.getX() + " " + down_y + " " + (int) event.getY());
                case MotionEvent.ACTION_MOVE:
                    Log.d("My move", down_x + " " + (int) event.getX() + " " + down_y + " " + (int) event.getY());
                case MotionEvent.ACTION_UP:
                    Log.d("My up", down_x + " " + (int) event.getX() + " " + down_y + " " + (int) event.getY());
                    int x = (int) event.getX();
                    int y = (int) event.getY();

                    if (down_x == x && down_y == y) {
                        int positionID = getClickPositionID(y);
                        if (positionID >= 0) {
                            lastId = openId;
                            openId = positionID;
                            startAnimator();
                        }
                    }

                    break;
                default:
                    break;
            }
        }

        return super.onTouchEvent(event);
    }

    int getClickPositionID(int y) {
        int positionId = -1;
        if ((openId + 1) * ITEM_TITLE_HEIGHT > y) {
            for (int i = 0; i <= openId; i++) {
                if (i * ITEM_TITLE_HEIGHT < y && (i + 1) * ITEM_TITLE_HEIGHT > y) {
                    positionId = i;
                    break;
                }
            }
        } else if ((openId + 1) * ITEM_TITLE_HEIGHT + ITEM_CONTENT_HEIGHT < y) {
            for (int i = openId + 1; i < getChildCount() - contentViews; i++) {
                if (i * ITEM_TITLE_HEIGHT + ITEM_CONTENT_HEIGHT < y && (i + 1) * ITEM_TITLE_HEIGHT + ITEM_CONTENT_HEIGHT > y) {
                    positionId = i;
                    break;
                }
            }
        } else {
            positionId = -1;
        }
        Log.d("My", positionId + "");
        return positionId;
    }

    public void startAnimator() {
        int s = Math.min(openId, lastId);
        int b = Math.max(openId, lastId);
        final int k = openId > lastId ? 1 : -1;
        set = new AnimatorSet();
        Set<Animator> animSet = new HashSet<>();

        if (s != b) {
            for (int i = s + 1; i <= b; i++) {
                View view = getChildAt(i);
                animSet.add(ObjectAnimator.ofFloat(view, "translationY", view.getTranslationY(), view.getTranslationY() - k * ITEM_CONTENT_HEIGHT));
            }

            final ObjectAnimator currentOa = ObjectAnimator.ofFloat(current, "scaleY", 1, 0);
            ObjectAnimator nextOa = ObjectAnimator.ofFloat(next, "scaleY", 0, 1);
            animSet.add(currentOa);
            animSet.add(nextOa);
            set.playTogether(animSet);
            set.setDuration(1000);
            set.setInterpolator(new AccelerateInterpolator());
            set.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    next.layout(left, (openId + 1) * ITEM_TITLE_HEIGHT, right, (openId + 1) * ITEM_TITLE_HEIGHT + ITEM_CONTENT_HEIGHT);
                    if (k > 0) {
                        current.setPivotY(0);
                        next.setPivotY(next.getHeight());
                    } else {
                        next.setPivotY(0);
                        current.setPivotY(current.getHeight());
                    }
//                    MyAdapterView.this.setClickable(false);
//                    MyAdapterView.this.setFocusable(false);
//                    MyAdapterView.this.setEnabled(false);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    //current.removeAllViews();
                    next.removeAllViews();
                    next.addView(baseAdapter.getView(openId, null, next));
                    last = current;
                    current = next;
                    next = last;
                    next.removeAllViews();
                    last = null;
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            set.start();
        }
    }

    @Override
    public void onClick(View v) {

    }
}
