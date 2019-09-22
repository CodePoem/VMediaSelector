package com.vdreamers.vmediaselector.ui.impl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.vdreamers.vmediaselector.core.contract.SelectorContract;
import com.vdreamers.vmediaselector.core.entity.MediaEntity;
import com.vdreamers.vmediaselector.core.impl.presenter.SelectorPresenter;
import com.vdreamers.vmediaselector.core.option.SelectorOptions;
import com.vdreamers.vmediaselector.core.scope.MediaTypeConstants;
import com.vdreamers.vmediaselector.ui.BaseMediaActivity;
import com.vdreamers.vmediaselector.ui.BaseMediaViewFragment;

import java.util.ArrayList;
import java.util.List;


/**
 * 默认UI Activity
 * Default UI Activity for simplest usage.
 * A simple subclass of {@link BaseMediaActivity}. Holding a {@link BaseMediaViewFragment} to
 * display medias.
 * <p>
 * date 2019-09-18 22:39:18
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
public class MediaActivity extends BaseMediaActivity {
    private MediaFragment mPickerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v_selector_ui_impl_activity_mdiea);
        createToolbar();
        setTitleTxt(getSelectorOptions());
    }

    @Override
    public SelectorContract.Presenter createPresenter(SelectorContract.View view) {
        return new SelectorPresenter(view);
    }

    @NonNull
    @Override
    public MediaFragment onCreateMediaView(ArrayList<MediaEntity> medias) {
        mPickerFragment =
                (MediaFragment) getSupportFragmentManager().findFragmentByTag(MediaFragment.TAG);
        if (mPickerFragment == null) {
            mPickerFragment =
                    (MediaFragment) MediaFragment.newInstance().setSelectedBundle(medias);
            getSupportFragmentManager().beginTransaction().replace(R.id.content_layout,
                    mPickerFragment, MediaFragment.TAG).commit();
        }
        return mPickerFragment;
    }

    private void createToolbar() {
        Toolbar bar = (Toolbar) findViewById(R.id.nav_top_bar);
        setSupportActionBar(bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setTitleTxt(SelectorOptions options) {
        TextView titleTxt = (TextView) findViewById(R.id.pick_album_txt);
        if (options.getMode() == MediaTypeConstants.MEDIA_TYPE_VIDEO) {
            titleTxt.setText(R.string.v_selector_ui_impl_video_title);
            titleTxt.setCompoundDrawables(null, null, null, null);
            return;
        }
        mPickerFragment.setTitleTxt(titleTxt);
    }

    @Override
    public void onMediaSelectorFinish(Intent intent, @Nullable List<MediaEntity> medias) {
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
