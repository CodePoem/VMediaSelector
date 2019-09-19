package com.vdreamers.vmediaselector.sample;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.vdreamers.vmediaselector.MediaSelectorUtils;
import com.vdreamers.vmediaselector.core.callback.MediaSelectCallback;
import com.vdreamers.vmediaselector.core.entity.MediaEntity;
import com.vdreamers.vmediaselector.obtain.ImageObtainUtils;
import com.vdreamers.vmediaselector.obtain.ObtainListener;
import com.vdreamers.vmediaselector.sample.adapter.MediaGridInset;
import com.vdreamers.vmediaselector.sample.adapter.MediaSelectResultsAdapter;
import com.vdreamers.vmediaselector.sample.custom.DefaultMediaSelectorImpl;

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

    private RadioGroup mRgNeedCamera;
    private RadioGroup mRgMultiSelectable;

    private RecyclerView mRecyclerView;
    private MediaSelectResultsAdapter mAdapter;
    private ArrayList<Uri> mMediaUriList;
    private ArrayList<String> mMediaPathList;
    private ArrayList<MediaEntity> mMediaEntities;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_sample);


        findViewById(R.id.btn_select).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        mTvPath = findViewById(R.id.tv_path);

        mRgNeedCamera = findViewById(R.id.rg_need_camera);
        mRgMultiSelectable = findViewById(R.id.rg_multi_selectable);

        mRgNeedCamera.check(R.id.rb_need_camera);
        mRgMultiSelectable.check(R.id.rb_multi_select);

        mRecyclerView = findViewById(R.id.rv_media);
        mAdapter = new MediaSelectResultsAdapter(mRecyclerView);
        mAdapter.setItemDeleteListener(new MediaSelectResultsAdapter.ItemDeleteListener() {
            @Override
            public void onDelete(int position) {
                if (mMediaEntities != null) {
                    mMediaEntities.remove(position);
                }
                if (mMediaUriList != null) {
                    mMediaUriList.remove(position);
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
                .setImageMultiSelected(mRgMultiSelectable.getCheckedRadioButtonId() == R.id.rb_multi_select)
                .selectImage(SampleActivity.this, new MediaSelectCallback() {
                    @Override
                    public void onMediaSelectSuccess(int resultCode, Intent data,
                                                     List<MediaEntity> medias) {
                        if (mAdapter == null || mRecyclerView == null) {
                            return;
                        }
                        mMediaEntities = (ArrayList<MediaEntity>) medias;
                        List<Uri> mediaUriList = getUriListFromMediaList(medias);
                        mMediaUriList = (ArrayList<Uri>) mediaUriList;
                        obtainPathListFromUriList(SampleActivity.this, mediaUriList);
                    }

                    @Override
                    public void onMediaSelectError(Throwable mediaSelectError) {

                    }
                }, mMediaEntities);
    }

    private List<Uri> getUriListFromMediaList(List<MediaEntity> medias) {
        List<Uri> mediaUriList = new ArrayList<>();
        if (medias != null) {
            for (MediaEntity media : medias) {
                if (media == null) {
                    continue;
                }
                mediaUriList.add(media.getUri());
            }
        }
        return mediaUriList;
    }

    private void obtainPathListFromUriList(final Context context, final List<Uri> mediaUriList) {
        final ProgressDialog[] progressDialog = new ProgressDialog[1];
        ImageObtainUtils.of()
                .setCallback(new ObtainListener() {
                    @Override
                    public void onStart() {
                        if (context != null && progressDialog[0] == null) {
                            progressDialog[0] = ProgressDialog.show(context, "",
                                    context.getString(R.string.text_tip_obtain_ing));
                        }
                    }

                    @Override
                    public void onSuccess(List<String> obtainFilePathList) {
                        if (progressDialog[0] != null) {
                            progressDialog[0].dismiss();
                        }
                        mAdapter.setList(obtainFilePathList);
                        mMediaPathList = (ArrayList<String>) obtainFilePathList;
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        if (progressDialog[0] != null) {
                            progressDialog[0].dismiss();
                        }
                    }
                }).obtain(context, mediaUriList);
    }
}
