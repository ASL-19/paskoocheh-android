package org.asl19.paskoocheh.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

/**
 * A view that shows items in a vertically non-scrolling list. The items
 * come from the ListAdapter associated with this view.
 *
 * @author http://stackoverflow.com/questions/18813296/non-scrollable-listview-inside-scrollview/24629341#24629341
 */
public class NonScrollExpandableListView extends ExpandableListView {

    /**
     * Generate a non-scrollable list view.
     *
     * @param context A Context instance.
     */
    public NonScrollExpandableListView(Context context) {
        super(context);
    }

    /**
     * Generate a non-scrollable list view.
     *
     * @param context A Context instane.
     * @param attrs The desired attributes to be retrieved.
     */
    public NonScrollExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Generate a non-scrollable list view.
     *
     * @param context A Context instance.
     * @param attrs The desired attributes to be retrieved.
     * @param defStyle An attribute in the current theme that contains a
     *                     reference to a style resource that supplies
     *                     defaults values for the TypedArray.  Can be
     *                     0 to not look for defaults.
     */
    public NonScrollExpandableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMeasureSpecCustom = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpecCustom);
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = getMeasuredHeight();
    }
}