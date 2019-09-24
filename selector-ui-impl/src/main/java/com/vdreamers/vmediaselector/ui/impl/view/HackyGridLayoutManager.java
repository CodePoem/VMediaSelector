package com.vdreamers.vmediaselector.ui.impl.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


/**
 * HackyGridLayoutManager
 * <p>
 * date 2019-09-18 21:54:37
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
public class HackyGridLayoutManager extends GridLayoutManager {
    public HackyGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr,
                                  int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public HackyGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public HackyGridLayoutManager(Context context, int spanCount, int orientation,
                                  boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }
}
