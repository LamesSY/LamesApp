package com.lames.standard.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

public class PointTextView extends androidx.appcompat.widget.AppCompatTextView {
    private static final String TAG = "SpecialTextView";

    public PointTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        handleLeftDrawable();
    }

    private void handleLeftDrawable() {
        Drawable leftDrawable = getCompoundDrawables()[0];
        if (leftDrawable == null) {
            return;
        }
        //获取实际行数
        int lineCount = Math.min(getLineCount(), getMaxLines());
        //获取文本高度
        int vsPace = getBottom() - getTop() - getCompoundPaddingBottom() - getCompoundPaddingTop();
        //计算位置差值
        int verticalOffset = (int) (-1 * (vsPace * (1 - 1.0f / lineCount)) / 2);
        //重新设置Bounds
        leftDrawable.setBounds(0, verticalOffset, leftDrawable.getIntrinsicWidth(),
                leftDrawable.getIntrinsicHeight() + verticalOffset);
    }
}