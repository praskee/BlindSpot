package com.winthishackathon.xd.blindspot.indoorwayMapPackage;


import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by Tomasz on 13/01/2018.
 */

public class CustomGestureDetector implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener{

    @Override
    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {

        Log.e("CustomGesture", "SingleTap detected");
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent motionEvent) {
        Log.e("CustomGesture", "DoubleTap detected");
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
        Log.e("CustomGesture", "DoubleTapEvent detected");
        return true;
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
        Log.e("CustomGesture", "LongPress detected");
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }
}
