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
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.glutils.FileTextureData;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.esotericsoftware.spine.SkeletonRendererDebug;
import com.esotericsoftware.spine.Skin;
import com.esotericsoftware.spine.Slot;
import com.esotericsoftware.spine.SlotData;
import com.esotericsoftware.spine.attachments.Attachment;
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
    Skeleton manSkeleton, womanSkeleton;
    AnimationState manState, womanState;
    Skin manSkin, womanSkin;

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
            TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("goblinWoman/goblins-pma.atlas"));
            SkeletonJson json = new SkeletonJson(atlas);
            json.setScale(2f);
            SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal("goblinWoman/goblins-woman-pro.json"));
            womanSkeleton = new Skeleton(skeletonData);
            womanSkeleton.setPosition(500, 300);
            womanSkeleton.setSkin("goblin");
            womanSkin = womanSkeleton.getSkin();

            AnimationStateData stateData = new AnimationStateData(skeletonData);

            womanState = new AnimationState(stateData);
            womanState.setAnimation(0, "walk", true);
        }

        {
            TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("goblinMan/goblins-pma.atlas"));
            SkeletonJson json = new SkeletonJson(atlas);
            json.setScale(2f);
            SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal("goblinMan/goblins-man-pro.json"));
            manSkeleton = new Skeleton(skeletonData);
            manSkeleton.setPosition(500, 300);
            manSkeleton.setSkin("goblin");
            manSkin = manSkeleton.getSkin();
        }

        Slot womanSlot = womanSkeleton.findSlot("torso");
        Slot manSlot = manSkeleton.findSlot("torso");

        int index = manSlot.getData().getIndex();
//        Attachment manAttachment = manSkin.getAttachment(index, "torso");
//        Attachment womanAttachment = womanSkin.getAttachment(index, "torso");

        MeshAttachment manAttachment = (MeshAttachment) manSlot.getAttachment();
        MeshAttachment womanAttachment = (MeshAttachment) womanSlot.getAttachment();


        float u1 = womanAttachment.getRegion().getU();
        float v1 = womanAttachment.getRegion().getV();
        float u2 = womanAttachment.getRegion().getU2();
        float v2 = womanAttachment.getRegion().getV2();

        int texWidth = (int) (womanAttachment.getRegion().getTexture().getWidth() * (v2 - v1));
        int texHeight = (int) (womanAttachment.getRegion().getTexture().getHeight() * (u2 - u1));

        Texture tex = new Texture(Gdx.files.internal("goblinMan/body.png"));
        TextureRegion region = new TextureRegion();//womanAttachment.getRegion();

        region.setRegion(tex);
        region.setRegion(0f, 0f, 1, 1);
        region.setRegion(0, 0, texWidth, texHeight);

        womanAttachment.setRegion(region);
        womanAttachment.updateUVs();
        {
            //womanSlot.setAttachment(manAttachment);
            womanSlot.setAttachment(womanAttachment);
        }


        String time = getSystemTime();
        Log.d(TAG, "@zjh create: SysTime: " + time);
        long timeMap = ConvertTime2Timestamp(time);
        Log.d(TAG, "@zjh create: timeMap:" + timeMap);

    }

    @Override
    public void render() {
        womanState.update(Gdx.graphics.getDeltaTime());

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (womanState.apply(womanSkeleton)) // Poses manSkeleton using current animations. This sets the bones' local SRT.
            womanSkeleton.updateWorldTransform(); // Uses the bones' local SRT to compute their world SRT.

        camera.update();

        batch.getProjectionMatrix().set(camera.combined);
        batch.begin();
        renderer.draw(batch, womanSkeleton);
        batch.end();

//        debugRenderer.draw(womanSkeleton);
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
}
