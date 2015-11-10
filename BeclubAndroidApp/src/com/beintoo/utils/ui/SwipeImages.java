package com.beintoo.utils.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ViewFlipper;

import com.beintoo.beclubapp.R;

public class SwipeImages extends ViewFlipper {


    private Context mContext;


    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    public final GestureDetector gestureDetector = new GestureDetector(new GestureListener());

    private ViewFlipper mViewFlipper;


    public SwipeImages(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public SwipeImages(Context context) {
        super(context);
        mContext = context;
        init();
    }

    private void init(){
        mViewFlipper = this;

        this.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
            if (gestureDetector.onTouchEvent(motionEvent)) {
                return true;
            }
            return true;
            }


        });

    }

    class GestureListener extends GestureDetector.SimpleOnGestureListener {
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if(e1 == null || e2 == null) return false;

            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {

                Log.i("dfsd", "Right to left");

                mViewFlipper.setInAnimation(mContext, R.anim.in_from_right);
                mViewFlipper.setOutAnimation(mContext, R.anim.out_to_left);

                mViewFlipper.showNext();

                return false; // Right to left
            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {

                mViewFlipper.setInAnimation(mContext, R.anim.in_from_left);
                mViewFlipper.setOutAnimation(mContext, R.anim.out_to_right);

                mViewFlipper.showPrevious();

                return false; // Left to right
            }

            if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                return false; // Bottom to top
            } else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                return false; // Top to bottom
            }

            return false;
        }
    }

    /*@Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        super.dispatchTouchEvent(ev);
        return gestureDetector.onTouchEvent(ev);
    }*/


    public GestureDetector getGestureDetector() {
        return gestureDetector;
    }

    public void removeGestureDetector(){
        this.setOnTouchListener(null);
    }
}
