package com.vdreamers.vmediaselector.sample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.vdreamers.vmediaselector.MediaSelectorUtils;
import com.vdreamers.vmediaselector.core.callback.MediaSelectUriCallback;
import com.vdreamers.vmediaselector.sample.custom.DefaultMediaSelectorImpl;

import java.util.List;

/**
 * Sample
 * <p>
 * date 2019/09/18 20:19:55
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
public class SampleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        Button selectBtn = findViewById(R.id.btn_select);
        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                init();
            }
        });
    }


    private void init() {
        initSelector();
    }

    private void initSelector() {
        MyMediaSelectCallback myMediaSelectCallback = new MyMediaSelectCallback();
        MediaSelectorUtils.of(new DefaultMediaSelectorImpl())
                .selectImage(SampleActivity.this, myMediaSelectCallback);
    }

    private static class MyMediaSelectCallback implements MediaSelectUriCallback {

        @Override
        public void onMediaSelectSuccess(int resultCode, Intent data, List<Uri> uris) {

        }

        @Override
        public void onMediaSelectError(Throwable mediaSelectError) {

        }
    }
}
