package com.vdreamers.vmediaselector.sample.custom;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.vdreamers.vmediaselector.core.entity.MediaEntity;
import com.vdreamers.vmediaselector.core.loader.MediaLoader;
import com.vdreamers.vmediaselector.core.option.SelectorOptions;
import com.vdreamers.vmediaselector.core.selector.IMediaSelector;
import com.vdreamers.vmediaselector.core.selector.MediaSelector;
import com.vdreamers.vmediaselector.ui.impl.MediaActivity;

import java.util.List;

/**
 * 默认图片选择器实现
 * <p>
 * date 2019-09-18 21:05:50
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
public class DefaultMediaSelectorImpl implements IMediaSelector {

    @Override
    public void init() {
        MediaLoader.getInstance().init(new DefaultGlideLoader());
    }

    @Override
    public void apply(@NonNull Activity activity, @Nullable Fragment fragment,
                      SelectorOptions selectorOptions) {
        if (selectorOptions == null) {
            return;
        }
    }

    @Override
    public Intent getIntent(@NonNull Activity activity, SelectorOptions selectorOptions) {
        Intent intent = new Intent(activity, MediaActivity.class);
        return intent;
    }

    @Override
    public List<MediaEntity> getResult(Intent data) {
        if (data != null) {
            return data.getParcelableArrayListExtra(MediaSelector.EXTRA_RESULT);
        }
        return null;
    }
}
