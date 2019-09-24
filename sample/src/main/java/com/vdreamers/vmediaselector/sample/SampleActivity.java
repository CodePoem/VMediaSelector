package com.vdreamers.vmediaselector.sample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vdreamers.vmediaselector.MediaSelectFilesCallback;
import com.vdreamers.vmediaselector.MediaSelectorUtils;
import com.vdreamers.vmediaselector.sample.adapter.MediaGridInset;
import com.vdreamers.vmediaselector.sample.adapter.MediaSelectResultsAdapter;
import com.vdreamers.vmediaselector.sample.custom.DefaultMediaSelectorImpl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * SampleActivity
 * <p>
 * date 2019/09/19 14:13:31
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
public class SampleActivity extends AppCompatActivity {

    private TextView mTvPath;

    private RadioGroup mRgType;
    private RadioGroup mRgNeedCamera;
    private RadioGroup mRgMultiSelectable;

    private RecyclerView mRecyclerView;
    private MediaSelectResultsAdapter mAdapter;
    private ArrayList<String> mMediaPathList;
    private ArrayList<Uri> mMediaUris;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_sample);


        findViewById(R.id.btn_select).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRgType.getCheckedRadioButtonId() == R.id.rb_image) {
                    selectImage();
                } else {
                    selectVideo();
                }
            }
        });

        mTvPath = findViewById(R.id.tv_path);

        mRgType = findViewById(R.id.rg_type);
        mRgNeedCamera = findViewById(R.id.rg_need_camera);
        mRgMultiSelectable = findViewById(R.id.rg_multi_selectable);

        mRgType.check(R.id.rb_image);
        mRgNeedCamera.check(R.id.rb_need_camera);
        mRgMultiSelectable.check(R.id.rb_multi_select);

        mRecyclerView = findViewById(R.id.rv_media);
        mAdapter = new MediaSelectResultsAdapter(mRecyclerView);
        mAdapter.setItemDeleteListener(new MediaSelectResultsAdapter.ItemDeleteListener() {
            @Override
            public void onDelete(int position) {
                if (mMediaUris != null) {
                    mMediaUris.remove(position);
                }
                if (mMediaPathList != null) {
                    mMediaPathList.remove(position);
                    if (mAdapter != null) {
                        mAdapter.setList(mMediaPathList);
                    }
                }
            }
        });
        int spacing = getResources().getDimensionPixelSize(R.dimen.media_grid_spacing);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new MediaGridInset(3, spacing, false));
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void selectImage() {
        MediaSelectorUtils.of(new DefaultMediaSelectorImpl())
                .setNeedCamera(mRgNeedCamera.getCheckedRadioButtonId() == R.id.rb_need_camera)
                .setMultiSelectable(mRgMultiSelectable.getCheckedRadioButtonId() == R.id.rb_multi_select)
                .setNeedGif(true)
                .selectImage(SampleActivity.this, mMediaUris, new MediaSelectFilesCallback() {
                    @Override
                    public void onSuccess(int resultCode, Intent data, List<Uri> uris,
                                          List<File> files) {
                        if (mAdapter == null || mRecyclerView == null) {
                            return;
                        }
                        if (mMediaPathList == null) {
                            mMediaPathList = new ArrayList<>();
                        }
                        mMediaPathList.clear();
                        for (File file : files) {
                            mMediaPathList.add(file.getAbsolutePath());
                        }
                        mMediaUris = (ArrayList<Uri>) uris;
                        mAdapter.setList(mMediaPathList);
                    }

                    @Override
                    public void onFailed(Throwable mediaSelectError) {

                    }
                });
    }

    private void selectVideo() {
        MediaSelectorUtils.of(new DefaultMediaSelectorImpl())
                .setNeedCamera(mRgNeedCamera.getCheckedRadioButtonId() == R.id.rb_need_camera)
                .setMultiSelectable(mRgMultiSelectable.getCheckedRadioButtonId() == R.id.rb_multi_select)
                .selectVideo(SampleActivity.this, new MediaSelectFilesCallback() {
                    @Override
                    public void onSuccess(int resultCode, Intent data, List<Uri> uris,
                                          List<File> files) {
                        if (mAdapter == null || mRecyclerView == null) {
                            return;
                        }
                        if (mMediaPathList == null) {
                            mMediaPathList = new ArrayList<>();
                        }
                        mMediaPathList.clear();
                        for (File file : files) {
                            mMediaPathList.add(file.getAbsolutePath());
                        }
                        mMediaUris = (ArrayList<Uri>) uris;
                        mAdapter.setList(mMediaPathList);
                    }

                    @Override
                    public void onFailed(Throwable mediaSelectError) {

                    }
                });
    }
}
