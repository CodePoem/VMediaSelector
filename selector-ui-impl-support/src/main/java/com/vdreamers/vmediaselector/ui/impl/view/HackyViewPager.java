package com.vdreamers.vmediaselector.ui.impl.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * HackyViewPager
 * https://github.com/chrisbanes/PhotoView/issues/35
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
