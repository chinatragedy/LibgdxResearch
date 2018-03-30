package com.zjh.gamelibgdx;

import android.util.Log;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.*;
import com.esotericsoftware.spine.attachments.Attachment;

/**
 * Created by ZhangJinghao on 2018/3/29.
 */

public class FirstGame implements ApplicationListener {

    private static final String TAG = "FirstGame";
    OrthographicCamera camera;
    PolygonSpriteBatch batch;
    TextureAtlas atlas;

    //from Spine
    SkeletonRenderer renderer;
    SkeletonRendererDebug debugRenderer;
    Skeleton skeleton;
    AnimationState animationState;

    @Override
    public void create() {
        camera = new OrthographicCamera();
        batch = new PolygonSpriteBatch();
        renderer = new SkeletonRenderer();
        renderer.setPremultipliedAlpha(true); // PMA results in correct blending without outlines.

        debugRenderer = new SkeletonRendererDebug();
        debugRenderer.setBoundingBoxes(false);
        debugRenderer.setRegionAttachments(false);

        atlas = new TextureAtlas(Gdx.files.internal("goblins/goblins-pma.atlas"));
        SkeletonJson json = new SkeletonJson(atlas); // This loads skeleton JSON data, which is stateless.
        json.setScale(1f); // Load the skeleton at 60% the size it was in Spine.
        SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal("goblins/goblins-ess.json"));

        skeleton = new Skeleton(skeletonData); // Skeleton holds skeleton animationState (bone positions, slot attachments, etc).
        skeleton.setPosition(500, 300);
        skeleton.setSkin("goblin");

        Skin skin = skeletonData.findSkin("goblingirl");



        /**设置物品配件*/
        // 按名称查找插槽。
        Slot slot = skeleton.findSlot("left-hand-item");
        // 按名称从骨架皮肤或默认皮肤获取附件。
        Attachment attachment = skeleton.getAttachment("right-hand-item2", "shield");
        // 设置插槽的附件。
        slot.setAttachment(attachment);

        AnimationStateData stateData = new AnimationStateData(skeletonData); // Defines mixing (crossfading) between animations.
        stateData.setDefaultMix(0.5f);

        animationState = new AnimationState(stateData); // Holds the animation animationState for a skeleton (current animation, time, etc).
        animationState.setTimeScale(0.8f); // Slow all animations down to 50% speed.

        animationState.setAnimation(0, "walk", true);
        //animationState.addAnimation(0, "jump", true, 0);

    }


    @Override
    public void resize(int i, int i1) {
        camera.setToOrtho(false); // Update camera with new size.
    }

    @Override
    public void render() {
        animationState.update(Gdx.graphics.getDeltaTime()); // Update the animation time.

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(0, 0, 0, 0f);

        animationState.apply(skeleton); // Poses skeleton using current animations. This sets the bones' local SRT.
        skeleton.updateWorldTransform(); // Uses the bones' local SRT to compute their world SRT.

        // Configure the camera, SpriteBatch, and SkeletonRendererDebug.
        camera.update();

        batch.getProjectionMatrix().set(camera.combined);
        debugRenderer.getShapeRenderer().setProjectionMatrix(camera.combined);

        batch.begin();
        renderer.draw(batch, skeleton); // Draw the skeleton images.
        batch.end();

        //debugRenderer.draw(skeleton); // Draw debug lines.
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

    public void mixPlayAnim(String anim) {
        if (null != animationState) {
            Log.d(TAG, "@zjh mixPlayAnim: " + animationState.getCurrent(0).getAnimation().getName());
            animationState.getData().setMix("", anim, 0.2f);
            animationState.addAnimation(0, anim, true, 0);
        }
    }

    public void PlayIdle() {
        if (null != animationState) {
            animationState.addAnimation(0, "idle", true, 0);
        }
    }
}
