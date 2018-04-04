package com.zjh.gamelibgdx;

import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.SurfaceView;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.zjh.gamelibgdx.demo.*;
import com.zjh.gamelibgdx.demo.SimpleTest1;

public class MainActivity extends AndroidApplication {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        setContentView(R.layout.activity_main);

        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.r = cfg.g = cfg.b = cfg.a = 8;

        initialize(new MixTest(),cfg);

        if (graphics.getView() instanceof SurfaceView) {
            SurfaceView glView = (SurfaceView) graphics.getView();
            glView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
            glView.setZOrderOnTop(true);
        }
    }
}
