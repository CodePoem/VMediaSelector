package com.vdreamers.vmediaselector;

import android.net.Uri;

import com.vdreamers.vmediaselector.core.entity.MediaEntity;

import java.util.LinkedHashMap;

public class SelectedHolder {

    private LinkedHashMap<Uri, MediaEntity> mSelectedMediaEntity;

    private SelectedHolder() {

    }

    private static class SingletonHolder {
        private static final SelectedHolder INSTANCE = new SelectedHolder();
    }

    public static SelectedHolder getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public LinkedHashMap<Uri, MediaEntity> getSelectedMediaEntity() {
        return mSelectedMediaEntity;
    }

    public void setSelectedMediaEntity(LinkedHashMap<Uri, MediaEntity> selectedMediaEntity) {
        mSelectedMediaEntity = selectedMediaEntity;
    }
}
