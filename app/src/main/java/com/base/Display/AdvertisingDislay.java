package com.base.Display;

import android.app.Presentation;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.SurfaceView;

import com.jelly.jellybase.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class AdvertisingDislay extends Presentation {
    private Unbinder unbinder;
    @BindView(R.id.video)
    SurfaceView video;
    public AdvertisingDislay(Context outerContext, Display display) {
        super(outerContext, display);
    }

    public AdvertisingDislay(Context outerContext, Display display, int theme) {
        super(outerContext, display, theme);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.advertising_dislay);
        unbinder= ButterKnife.bind(this);
    }
    public SurfaceView getVideo(){
        return video;
    }
    @Override
    public void show() {
        super.show();
        iniView();
    }

    private void iniView(){
    }

    @Override
    public void dismiss() {
        if (unbinder!=null)
            unbinder.unbind();
        super.dismiss();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
