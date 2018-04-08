package com.zjh.gamelibgdx;

import android.util.Log;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.esotericsoftware.spine.SkeletonRendererDebug;
import com.esotericsoftware.spine.Skin;
import com.esotericsoftware.spine.Slot;
import com.esotericsoftware.spine.attachments.MeshAttachment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ZhangJinghao on 2018/4/3.
 */

public class ChangeAppearanceTest implements ApplicationListener {

    private static final String TAG = "ChangeAppearanceTest";

    OrthographicCamera camera;
    PolygonSpriteBatch batch;

    SkeletonRenderer renderer;
    Skeleton skeleton;
    AnimationState animationState;
    Skin skin;

    //Debug
    SkeletonRendererDebug debugRenderer;

    @Override
    public void create() {
        camera = new OrthographicCamera();
        batch = new PolygonSpriteBatch();
        renderer = new SkeletonRenderer();
        renderer.setPremultipliedAlpha(true);
        // debug
        debugRenderer = new SkeletonRendererDebug();
        debugRenderer.setBoundingBoxes(false);
        debugRenderer.setRegionAttachments(false);
//        debugRenderer.setMeshTriangles(false);

        {
            TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("raptor/raptor-pma.atlas"));
            SkeletonJson json = new SkeletonJson(atlas);
            json.setScale(0.5f);
            SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal("raptor/raptor-pro.json"));
            skeleton = new Skeleton(skeletonData);

            skeleton.setPosition(500, 300);
            skeleton.setSkin("default");
            skin = skeleton.getSkin();

            AnimationStateData stateData = new AnimationStateData(skeletonData);
            stateData.setDefaultMix(0.5f);

            animationState = new AnimationState(stateData);
            animationState.setAnimation(0, "walk", true);
            animationState.addAnimation(0, "gun-grab", false, 5f);
            animationState.addAnimation(0, "gun-holster", false, 1f);
            animationState.addAnimation(0, "roar", false, 1f);
            animationState.addAnimation(0, "jump", true, 1f);
        }

//        {
//            TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("goblinMan/goblins-pma.atlas"));
//            SkeletonJson json = new SkeletonJson(atlas);
//            json.setScale(2f);
//            SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal("goblinMan/goblins-man-pro.json"));
//            manSkeleton = new Skeleton(skeletonData);
//            manSkeleton.setPosition(500, 300);
//            manSkeleton.setSkin("goblin");
//            manSkin = manSkeleton.getSkin();
//        }

        Slot womanSlot = skeleton.findSlot("head");
        MeshAttachment womanAttachment = (MeshAttachment) womanSlot.getAttachment();

        // 加载图片资源
        Texture tex = new Texture(Gdx.files.internal("raptor/head.png"));
        TextureRegion region = new TextureRegion();//womanAttachment.getRegion();

        region.setRegion(tex);
        region.setRegion(0f, 0f, 1, 1);
        region.setRegion(0, 0, tex.getWidth(), tex.getHeight());

        womanAttachment.setRegion(region);
        //更新UV，必须
        womanAttachment.updateUVs();
        {
            womanSlot.setAttachment(womanAttachment);
        }


        String time = getSystemTime();
        Log.d(TAG, "@zjh create: SysTime: " + time);
        long timeMap = ConvertTime2Timestamp(time);
        Log.d(TAG, "@zjh create: timeMap:" + timeMap);

        Log.d(TAG, "@zjh create: TimeMillis:"+System.currentTimeMillis()/1000);
    }

    @Override
    public void render() {
        animationState.update(Gdx.graphics.getDeltaTime());

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (animationState.apply(skeleton)) // Poses manSkeleton using current animations. This sets the bones' local SRT.
            skeleton.updateWorldTransform(); // Uses the bones' local SRT to compute their world SRT.

        camera.update();

        batch.getProjectionMatrix().set(camera.combined);
        batch.begin();
        renderer.draw(batch, skeleton);
        batch.end();

//        debugRenderer.draw(skeleton);
    }

    @Override
    public void resize(int i, int i1) {
        camera.setToOrtho(false); // Update camera with new size.
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }

    public static String getSystemTime() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat simplDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simplDateFormat.format(date);
    }

    public static long ConvertTime2Timestamp(String time) {
        long timeMap = 1522818158;
        //Date或者String转化为时间戳
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(time);
            timeMap = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeMap / 1000;
    }

    public static long getTimestamp() {

        long timeMap = 1522818158;
        Date date = new Date();
        date.getTime();
        return timeMap;
    }
}
