package com.vdreamers.vmediaselector.ui.impl.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.viewpager.widget.ViewPager;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;

/**
 * HackyViewPager
 * <p>
 * date 2019-09-18 21:58:06
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
public class HackyViewPager extends ViewPager {
    private boolean mIsLocked;

    public HackyViewPager(Context context) {
        super(context);
        mIsLocked = false;
    }

    public HackyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        mIsLocked = false;
    }

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if (v instanceof ImageViewTouch) {
            return ((ImageViewTouch) v).canScroll(dx) || super.canScroll(v, checkV, dx, x, y);
        }
        return super.canScroll(v, checkV, dx, x, y);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!mIsLocked) {
            try {
                return super.onInterceptTouchEvent(ev);
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return !mIsLocked && super.onTouchEvent(event);
    }

    public boolean isLocked() {
        return mIsLocked;
    }

    public void setLocked(boolean isLocked) {
        this.mIsLocked = isLocked;
    }

}
