package org.asl19.paskoocheh.utils;

import android.content.Context;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.AttributeSet;

public class NonScrollTextView extends AppCompatTextView {
    public NonScrollTextView(Context context) {
        super(context);
    }

    public NonScrollTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NonScrollTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void scrollTo(int x, int y) {
    }
}