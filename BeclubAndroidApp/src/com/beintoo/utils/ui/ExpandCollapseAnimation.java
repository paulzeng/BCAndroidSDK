package com.beintoo.utils.ui;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class ExpandCollapseAnimation  extends Animation{
    protected final int originalHeight;
    protected final View view;
    protected float perValue;

    public ExpandCollapseAnimation(final View view, int fromHeight, int toHeight) {
        fromHeight = (int) (fromHeight * view.getContext().getResources().getDisplayMetrics().density);
        toHeight = (int) (toHeight * view.getContext().getResources().getDisplayMetrics().density);
        this.view = view;
        this.originalHeight = fromHeight;
        this.perValue = (toHeight - fromHeight);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        view.getLayoutParams().height = (int) (originalHeight + perValue * interpolatedTime);
        view.requestLayout();
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}
