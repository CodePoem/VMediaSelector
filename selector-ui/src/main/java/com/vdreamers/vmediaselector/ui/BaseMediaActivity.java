package com.vdreamers.vmediaselector.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.vdreamers.vmediaselector.core.callback.OnMediaSelectorFinishListener;
import com.vdreamers.vmediaselector.core.contract.SelectorContract;
import com.vdreamers.vmediaselector.core.entity.MediaEntity;
import com.vdreamers.vmediaselector.core.option.SelectorOptions;
import com.vdreamers.vmediaselector.core.selector.MediaSelector;

import java.util.ArrayList;

/**
 * 连接视图层和逻辑层的基类Activity
 * 子类必须实现onCreateMediaView方法创建一个视图层
 * A abstract class to connect
 * {@link SelectorContract.View} and
 * {@link SelectorContract.Presenter}.
 * one job has to be done. override {@link #onCreateMediaView(ArrayList)} to create a subclass
 * for {@link BaseMediaViewFragment}.
 * <p>
 * date 2019-09-18 20:20:12
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
public abstract class BaseMediaActivity extends AppCompatActivity implements OnMediaSelectorFinishListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseMediaViewFragment view = onCreateMediaView(getSelectedMedias(getIntent()));
        view.setPresenter(createPresenter(view));
        view.setOnFinishListener(this);
    }

    /**
     * 根据视图层创建逻辑层
     *
     * @param view 视图层
     * @return 逻辑层
     */
    public abstract SelectorContract.Presenter createPresenter(SelectorContract.View view);

    private ArrayList<MediaEntity> getSelectedMedias(Intent intent) {
        if (intent == null) {
            return new ArrayList<>();
        }
        return intent.getParcelableArrayListExtra(MediaSelector.EXTRA_SELECTED_MEDIA);
    }

    public SelectorOptions getSelectorOptions() {
        return SelectorOptions.getInstance();
    }

    /**
     * create a {@link SelectorContract.View} attaching to
     * {@link SelectorContract.Presenter},call in {@link #onCreate(Bundle)}
     *
     * @param medias 多媒体列表
     * @return
     */
    @NonNull
    public abstract BaseMediaViewFragment onCreateMediaView(ArrayList<MediaEntity> medias);

}
