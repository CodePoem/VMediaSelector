package com.vdreamers.vmediaselector.ui.impl.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vdreamers.vmediaselector.core.entity.MediaEntity;
import com.vdreamers.vmediaselector.core.option.SelectorOptions;
import com.vdreamers.vmediaselector.ui.impl.MediaResHelper;
import com.vdreamers.vmediaselector.ui.impl.R;
import com.vdreamers.vmediaselector.ui.impl.view.MediaItemLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 多媒体适配器
 * <p>
 * date 2019-09-18 22:00:27
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
public class MediaAdapter extends RecyclerView.Adapter {
    private static final int CAMERA_TYPE = 0;
    private static final int NORMAL_TYPE = 1;

    private int mOffset;
    private boolean mMultiImageMode;

    private List<MediaEntity> mMedias;
    private List<MediaEntity> mSelectedMedias;
    private LayoutInflater mInflater;
    private SelectorOptions mSelectorOptions;
    private View.OnClickListener mOnCameraClickListener;
    private View.OnClickListener mOnMediaClickListener;
    private OnCheckListener mOnCheckListener;
    private OnMediaCheckedListener mOnCheckedListener;
    private int mDefaultRes;

    public MediaAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.mMedias = new ArrayList<>();
        this.mSelectedMedias = new ArrayList<>(9);
        this.mSelectorOptions = SelectorOptions.getInstance();
        this.mOffset = mSelectorOptions.isNeedCamera() ? 1 : 0;
        this.mMultiImageMode = mSelectorOptions.isMultiSelectable();
        this.mOnCheckListener = new OnCheckListener();
        this.mDefaultRes = MediaResHelper.getMediaPlaceHolderRes();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && mSelectorOptions.isNeedCamera()) {
            return CAMERA_TYPE;
        }
        return NORMAL_TYPE;
    }

    @Override
    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (CAMERA_TYPE == viewType) {
            return new CameraItemHolder(mInflater.inflate(R.layout.v_selector_ui_impl_layout_recycleview_meida_header, parent, false));
        }
        return new MediaItemHolder(mInflater.inflate(R.layout.v_selector_ui_impl_layout_recycleview_media_item,
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CameraItemHolder) {
            CameraItemHolder viewHolder = (CameraItemHolder) holder;
            viewHolder.mCameraLayout.setOnClickListener(mOnCameraClickListener);
            viewHolder.mCameraImg.setImageResource(MediaResHelper.getCameraRes());
        } else {
            int pos = position - mOffset;
            final MediaEntity media = mMedias.get(pos);
            final MediaItemHolder vh = (MediaItemHolder) holder;

            vh.mItemLayout.setImageRes(mDefaultRes);
            vh.mItemLayout.setTag(media);

            vh.mItemLayout.setOnClickListener(mOnMediaClickListener);
            vh.mItemLayout.setTag(R.id.media_item_check, pos);
            vh.mItemLayout.setMedia(media);
            vh.mItemChecked.setVisibility(mMultiImageMode ? View.VISIBLE : View.GONE);
            if (mMultiImageMode) {
                vh.mItemLayout.setChecked(media.isSelected());
                vh.mItemChecked.setTag(R.id.media_layout, vh.mItemLayout);
                vh.mItemChecked.setTag(media);
                vh.mItemChecked.setOnClickListener(mOnCheckListener);
            }
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mMedias.size() + mOffset;
    }

    public void setOnCameraClickListener(View.OnClickListener onCameraClickListener) {
        mOnCameraClickListener = onCameraClickListener;
    }

    public void setOnCheckedListener(OnMediaCheckedListener onCheckedListener) {
        mOnCheckedListener = onCheckedListener;
    }

    public void setOnMediaClickListener(View.OnClickListener onMediaClickListener) {
        mOnMediaClickListener = onMediaClickListener;
    }

    public List<MediaEntity> getSelectedMedias() {
        return mSelectedMedias;
    }

    public void setSelectedMedias(List<MediaEntity> selectedMedias) {
        if (selectedMedias == null) {
            return;
        }
        mSelectedMedias.clear();
        mSelectedMedias.addAll(selectedMedias);
        notifyDataSetChanged();
    }

    public void addAllData(@NonNull List<MediaEntity> data) {
        int oldSize = mMedias.size();
        this.mMedias.addAll(data);
        int size = data.size();
        notifyItemRangeInserted(oldSize, size);
    }

    public void clearData() {
        int size = mMedias.size();
        this.mMedias.clear();
        notifyItemRangeRemoved(0, size);
    }

    public List<MediaEntity> getAllMedias() {
        return mMedias;
    }

    private static class MediaItemHolder extends RecyclerView.ViewHolder {
        MediaItemLayout mItemLayout;
        View mItemChecked;

        MediaItemHolder(View itemView) {
            super(itemView);
            mItemLayout = itemView.findViewById(R.id.media_layout);
            mItemChecked = itemView.findViewById(R.id.media_item_check);
        }
    }

    private static class CameraItemHolder extends RecyclerView.ViewHolder {
        View mCameraLayout;
        ImageView mCameraImg;

        CameraItemHolder(final View itemView) {
            super(itemView);
            mCameraLayout = itemView.findViewById(R.id.camera_layout);
            mCameraImg = itemView.findViewById(R.id.camera_img);
        }
    }

    private class OnCheckListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            MediaItemLayout itemLayout = (MediaItemLayout) v.getTag(R.id.media_layout);
            MediaEntity media = (MediaEntity) v.getTag();
            if (mSelectorOptions.isMultiSelectable()) {
                if (mOnCheckedListener != null) {
                    mOnCheckedListener.onChecked(itemLayout, media);
                }
            }
        }
    }

    public interface OnMediaCheckedListener {
        /**
         * 选中回调
         * In multi image mode, selecting a {@link MediaEntity} or undo.
         *
         * @param v      选中的View
         * @param media 选中的多媒体
         */
        void onChecked(View v, MediaEntity media);
    }

}
