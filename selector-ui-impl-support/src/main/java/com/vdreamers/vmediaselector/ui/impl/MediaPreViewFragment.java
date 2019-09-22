package com.vdreamers.vmediaselector.ui.impl;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.vdreamers.vmediaselector.core.entity.MediaEntity;
import com.vdreamers.vmediaselector.core.scope.MediaTypeConstants;
import com.vdreamers.vmediaselector.ui.BaseMediaViewActivity;
import com.vdreamers.vmediaselector.ui.impl.utils.PhotoMetadataUtils;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;

/**
 * 展示原图Fragment
 * show raw image with the control of finger gesture.
 * <p>
 * date 2019-09-18 22:38:59
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
public class MediaPreViewFragment extends Fragment {
    /**
     * bundle传参-Image
     */
    private static final String BUNDLE_IMAGE = "MediaEntity";

    public static MediaPreViewFragment newInstance(@NonNull MediaEntity mediaEntity) {
        MediaPreViewFragment fragment = new MediaPreViewFragment();
        Bundle args = new Bundle();
        args.putParcelable(BUNDLE_IMAGE, mediaEntity);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.v_selector_ui_impl_fragment_raw_image, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final MediaEntity media = getArguments().getParcelable(BUNDLE_IMAGE);
        if (media == null) {
            return;
        }

        View videoPlayButton = view.findViewById(R.id.iv_video_play);
        if (media.getType() == MediaTypeConstants.MEDIA_TYPE_VIDEO) {
            videoPlayButton.setVisibility(View.VISIBLE);
            videoPlayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(media.getUri(), "video/*");
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(getContext(),
                                R.string.v_selector_ui_impl_error_no_video_activity,
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            videoPlayButton.setVisibility(View.GONE);
        }

        ImageViewTouch image = (ImageViewTouch) view.findViewById(R.id.iv_previw);
        image.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);

        Point point = PhotoMetadataUtils.getBitmapSize(media.getUri(), getActivity());
        if (getActivity() != null) {
            ((BaseMediaViewActivity) getActivity()).loadRawImage(image, media,
                    point.x, point.y);
        }
    }

    public void resetView() {
        if (getView() != null) {
            ((ImageViewTouch) getView().findViewById(R.id.iv_previw)).resetMatrix();
        }
    }

    private MediaPreViewActivity getThisActivity() {
        Activity activity = getActivity();
        if (activity instanceof MediaPreViewActivity) {
            return (MediaPreViewActivity) activity;
        }
        return null;
    }

}
