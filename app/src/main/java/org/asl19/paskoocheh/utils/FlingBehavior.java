package org.asl19.paskoocheh.utils;

import android.content.Context;
import com.google.android.material.appbar.AppBarLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Fling Behaviour Motion Adjuster.
 * @author http://stackoverflow.com/questions/30923889/flinging-with-recyclerview-appbarlayout
 */
public final class FlingBehavior extends AppBarLayout.Behavior {
    private static final int TOP_CHILD_FLING_THRESHOLD = 3;
    private boolean isPositive;

    /**
     * Nested Fling Behaviour.
     */
    public FlingBehavior() {
    }

    /**
     * Nested Fling Behaviour.
     *
     * @param context A Context instance.
     * @param attrs The desired attributes to be retrieved.
     */
    public FlingBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onNestedFling(
            CoordinatorLayout coordinatorLayout, AppBarLayout child, View target,
            float velocityX, float velocityY, boolean consumed) {
        float velocityYAdjusted = velocityY;
        boolean consumedAdjusted = consumed;

        if (velocityY > 0 && !isPositive || velocityY < 0 && isPositive) {
            velocityYAdjusted = velocityY * -1;
        }
        if (target instanceof RecyclerView && velocityY < 0) {
            final RecyclerView recyclerView = (RecyclerView) target;
            final View firstChild = recyclerView.getChildAt(0);
            final int childAdapterPosition = recyclerView.getChildAdapterPosition(firstChild);
            consumedAdjusted = childAdapterPosition > TOP_CHILD_FLING_THRESHOLD;
        }

        return super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityYAdjusted, consumedAdjusted);
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child,
                                  View target, int dx, int dy, int[] consumed) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
        isPositive = dy > 0;
    }
}