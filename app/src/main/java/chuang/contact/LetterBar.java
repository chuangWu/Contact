package chuang.contact;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class LetterBar extends LinearLayout {
    private String[] letters;
    private OnLetterTouchListener letterTouchListener;

    private float mLastX;

    public LetterBar(Context context) {
        super(context);
        init(context);
    }

    public LetterBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LetterBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public void init(Context context) {

    }

    private float itemHeight = -1;
    private Paint paint;
    private Bitmap letterBitmap;

    @Override
    protected void onDraw(Canvas canvas) {
        if (letters == null) {
            return;
        }
        if (itemHeight == -1) {
            itemHeight = getHeight() / letters.length;
        }
        if (paint == null) {

            paint = new Paint();
            paint.setTextSize(itemHeight - 4);
            paint.setColor(Color.parseColor("#ff000000"));
            paint.setFlags(Paint.ANTI_ALIAS_FLAG);
            Canvas mCanvas = new Canvas();
            letterBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            mCanvas.setBitmap(letterBitmap);
            float widthCenter = getMeasuredWidth() / 1.5F;

            for (int i = 0; i < letters.length; i++) {
                mCanvas.drawText(letters[i], widthCenter - paint.measureText(letters[i]) / 2, itemHeight * i + itemHeight, paint);
            }
        }
        if (letterBitmap != null) {
            canvas.drawBitmap(letterBitmap, 0, 0, paint);
        }
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        if (letterTouchListener == null || letters == null) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                mLastX = event.getX();

            }
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(mLastX - event.getX()) > 10f) {
                    letterTouchListener.onActionUp();
                    return true;
                }

                int position = (int) (event.getY() / itemHeight + 1);
                if (position > 0 && position < letters.length + 1) {
                    letterTouchListener.onLetterTouch(letters[position - 1], position);
                }
                return true;
            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_UP:
                letterTouchListener.onActionUp();
                return true;
        }
        return false;
    }

    public void setShowString(String[] letters) {
        this.letters = letters;
    }

    public void setOnLetterTouchListener(OnLetterTouchListener listener) {
        this.letterTouchListener = listener;
    }

    public interface OnLetterTouchListener {

        public void onLetterTouch(String letter, int position);

        public void onActionUp();
    }
}