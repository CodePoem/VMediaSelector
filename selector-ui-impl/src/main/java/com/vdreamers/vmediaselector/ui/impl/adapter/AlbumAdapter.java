package com.vdreamers.vmediaselector.ui.impl.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vdreamers.vmediaselector.core.entity.AlbumEntity;
import com.vdreamers.vmediaselector.core.entity.ImageMediaEntity;
import com.vdreamers.vmediaselector.core.loader.MediaLoader;
import com.vdreamers.vmediaselector.ui.impl.MediaResHelper;
import com.vdreamers.vmediaselector.ui.impl.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 相册适配器
 * Album window adapter.
 * <p>
 * date 2019-09-18 21:38:48
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
public class AlbumAdapter extends RecyclerView.Adapter implements View.OnClickListener {
    /**
     * 未知相册名
     */
    private static final String UNKNOWN_ALBUM_NAME = "?";
    /**
     * 当前相册位置
     */
    private int mCurrentAlbumPos;

    private List<AlbumEntity> mAlums;
    private LayoutInflater mInflater;
    private OnAlbumClickListener mAlbumOnClickListener;
    private int mDefaultRes;

    public AlbumAdapter(Context context) {
        this.mAlums = new ArrayList<>();
        this.mAlums.add(AlbumEntity.createDefaultAlbum());
        this.mInflater = LayoutInflater.from(context);
        this.mDefaultRes = MediaResHelper.getAlbumPlaceHolderRes();
    }

    public void setAlbumOnClickListener(OnAlbumClickListener albumOnClickListener) {
        this.mAlbumOnClickListener = albumOnClickListener;
    }

    @Override
    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AlbumViewHolder(mInflater.inflate(R.layout.v_selector_ui_impl_layout_album_item, parent,
                false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final AlbumViewHolder albumViewHolder = (AlbumViewHolder) holder;
        albumViewHolder.mCoverImg.setImageResource(mDefaultRes);
        final int adapterPos = holder.getAdapterPosition();
        final AlbumEntity album = mAlums.get(adapterPos);

        if (album != null && album.hasImages()) {
            String albumName = TextUtils.isEmpty(album.mBucketName) ?
                    albumViewHolder.mNameTxt.getContext().getString(R.string.v_selector_ui_impl_default_album_name) : album.mBucketName;
            albumViewHolder.mNameTxt.setText(albumName);
            ImageMediaEntity media = (ImageMediaEntity) album.mImageList.get(0);
            if (media != null) {
                MediaLoader.getInstance().displayThumbnail(albumViewHolder.mCoverImg,
                        media, 50, 50);
                albumViewHolder.mCoverImg.setTag(R.string.v_selector_ui_impl_app_name,
                        media);
            }
            albumViewHolder.mLayout.setTag(adapterPos);
            albumViewHolder.mLayout.setOnClickListener(this);
            albumViewHolder.mCheckedImg.setVisibility(album.mIsSelected ? View.VISIBLE : View.GONE);
            albumViewHolder.mSizeTxt.setText(albumViewHolder.mSizeTxt.
                    getResources().getString(R.string.v_selector_ui_impl_album_images_fmt,
                    album.mCount));
        } else {
            albumViewHolder.mNameTxt.setText(UNKNOWN_ALBUM_NAME);
            albumViewHolder.mSizeTxt.setVisibility(View.GONE);
        }
    }

    public void addAllData(List<AlbumEntity> alums) {
        mAlums.clear();
        mAlums.addAll(alums);
        notifyDataSetChanged();
    }

    public List<AlbumEntity> getAlums() {
        return mAlums;
    }

    public int getCurrentAlbumPos() {
        return mCurrentAlbumPos;
    }

    public void setCurrentAlbumPos(int currentAlbumPos) {
        mCurrentAlbumPos = currentAlbumPos;
    }

    public AlbumEntity getCurrentAlbum() {
        if (mAlums == null || mAlums.size() <= 0) {
            return null;
        }
        return mAlums.get(mCurrentAlbumPos);
    }

    @Override
    public int getItemCount() {
        return mAlums != null ? mAlums.size() : 0;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.album_layout) {
            if (mAlbumOnClickListener != null) {
                mAlbumOnClickListener.onClick(v, (Integer) v.getTag());
            }
        }
    }

    private static class AlbumViewHolder extends RecyclerView.ViewHolder {
        ImageView mCoverImg;
        TextView mNameTxt;
        TextView mSizeTxt;
        View mLayout;
        ImageView mCheckedImg;

        AlbumViewHolder(final View itemView) {
            super(itemView);
            mCoverImg = itemView.findViewById(R.id.album_thumbnail);
            mNameTxt = itemView.findViewById(R.id.album_name);
            mSizeTxt = itemView.findViewById(R.id.album_size);
            mLayout = itemView.findViewById(R.id.album_layout);
            mCheckedImg = itemView.findViewById(R.id.album_checked);
        }
    }

    public interface OnAlbumClickListener {
        /**
         * 相册点击回调
         * @param view 点击的View
         * @param pos 点击的位置索引
         */
        void onClick(View view, int pos);
    }
}
