package com.vdreamers.vmediaselector.ui.impl;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Fragment基类
 * <p>
 * date 2019-09-18 22:39:08
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
public class MediaBaseFragment extends Fragment {
    private boolean mNeedPendingUserVisibleHint;
    private boolean mLastUserVisibleHint;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mNeedPendingUserVisibleHint) {
            setUserVisibleCompat(mLastUserVisibleHint);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getActivity() == null) {
            mNeedPendingUserVisibleHint = true;
            mLastUserVisibleHint = isVisibleToUser;
        } else {
            setUserVisibleCompat(isVisibleToUser);
        }
    }

    void setUserVisibleCompat(boolean userVisibleCompat) {
    }
}
