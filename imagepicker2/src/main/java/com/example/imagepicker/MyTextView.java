package com.example.imagepicker;

import android.app.Notification;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;



public class MyTextView extends AppCompatTextView {

    Drawable drawableLeft;
    Drawable drawableRight;

    Drawable drawableTop;
    Drawable drawableBottom;

    int actionX;
    int actionY;
    public DrawableClickListener listener;

    public MyTextView(@NonNull Context context) {
        super(context);
    }

    public MyTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setCompoundDrawables(@Nullable Drawable left, @Nullable Drawable top, @Nullable Drawable right, @Nullable Drawable bottom) {

        if (right != null) {

            drawableRight = right;

        }

        if (left != null) {

            drawableLeft = left;
        }

        if (top != null) {

            drawableTop = top;
        }

        if (bottom != null) {

            drawableBottom = bottom;
        }



        super.setCompoundDrawables(left , top , right , bottom);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        Rect bounds = null;

        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            actionX = (int) event.getX();
            actionY = (int) event.getY();

            Rect rect = new Rect(getWidth()-60,0,getWidth(),getHeight());

            if (drawableRight != null) {

                 bounds = drawableRight.getBounds();


            }


//            Rect tappableArea = this.getClipBounds();
//            int extraTapArea = 13;
//
//            int x = actionX + extraTapArea;
//            int y = actionY-extraTapArea;
//
//            x = getWidth() -x;
//
//            if (x<=0) {
//
//                x = x+extraTapArea;
//            }
//
//            if (y<=0) {
//
//                y = actionY;
//            }

            if (rect.contains(actionX, actionY)) {

                if (listener!= null) {

                    listener.onClick(DrawableClickListener.DrawablePosition.RIGHT);



                }



                return true;

            }


        }

        return super.onTouchEvent(event);
    }


        public void setOnDrawableClickListener (DrawableClickListener listener){

            this.listener = listener;


        }

    @Override
    protected void finalize() throws Throwable {

        drawableRight = null;
        drawableLeft = null;
        drawableTop = null;
        drawableBottom = null;

        finalize();

    }

    public interface DrawableClickListener{

        enum DrawablePosition{LEFT , RIGHT}

        public void onClick(DrawablePosition target);
    }
}
