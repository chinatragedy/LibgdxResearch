package com.zjh.gamelibgdx;

import android.util.Log;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
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
        debugRenderer.setBoundingBoxes(true);
        debugRenderer.setRegionAttachments(true);

        atlas = new TextureAtlas(Gdx.files.internal("goblins/goblins-pma.atlas"));///Hero/hero-mesh.atlas
        SkeletonJson json = new SkeletonJson(atlas); // This loads manSkeleton JSON data, which is stateless.
        json.setScale(2f); // Load the manSkeleton at 60% the size it was in Spine.
        SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal("goblins/goblins-pro.json"));///Hero/hero-mesh.json
        skeleton = new Skeleton(skeletonData); // Skeleton holds manSkeleton animationState (bone positions, slot attachments, etc).
        skeleton.setPosition(500, 300);
        skeleton.setSkin("goblingirl");

//        Skin newSkin = skeletonData.findSkin("goblin");
//
//        /**设置物品配件*/
//        // 在json文件中找到身体的插槽名，和贴图名
//        String slotName = "left-foot";
//        String attachmentName = "left-foot";
//        // 按名称从骨架中找到对应插槽的index
//        int index = manSkeleton.findSlot(slotName).getData().getIndex();
//        // 在皮肤中找到index对应的贴图
//        Attachment attachment = newSkin.getAttachment(index, attachmentName);
//        // 将贴图插入插槽
//        manSkeleton.findSlot(slotName).setAttachment(attachment);
//        // 将贴图添加到当前皮肤
//        manSkeleton.getSkin().addAttachment(index, attachmentName, attachment);


        AnimationStateData stateData = new AnimationStateData(skeletonData); // Defines mixing (crossfading) between animations.
        stateData.setDefaultMix(0.5f);

        animationState = new AnimationState(stateData); // Holds the animation animationState for a manSkeleton (current animation, time, etc).
        animationState.setTimeScale(0.8f); // Slow all animations down to 50% speed.

        animationState.setAnimation(0, "walk", true);


        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {

                Log.d(TAG, "@zjh touchDown: ");

                TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("goblins/goblins-pma.atlas"));
                SkeletonJson json = new SkeletonJson(atlas); // This loads manSkeleton JSON data, which is stateless.
                json.setScale(2f);
                SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal("goblins/goblins-pro.json"));
                Skeleton skeleton2 = new Skeleton(skeletonData);
                Skin skin2 = skeletonData.findSkin("goblin");


                /**设置物品配件*/
                // 在json文件中找到身体的插槽名，和贴图名
                String slotName = "left-foot";
                String attachmentName = "left-foot";
                // 按名称从骨架中找到对应插槽的index
                int index = skeleton2.findSlot(slotName).getData().getIndex();
                // 在皮肤中找到index对应的贴图
                Attachment attachment = skin2.getAttachment(index, attachmentName);
                // 将贴图插入插槽
                skeleton.findSlot("left-foot").setAttachment(attachment);
                // 将贴图添加到当前皮肤
                skeleton.getSkin().addAttachment(index, attachmentName, attachment);

                AnimationStateData stateData = new AnimationStateData(skeleton.getData()); // Defines mixing (crossfading) between animations.
                stateData.setDefaultMix(0.5f);

                animationState = new AnimationState(stateData);
                animationState.setAnimation(0, "walk", true);

                return true;
            }
        });
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

        animationState.apply(skeleton); // Poses manSkeleton using current animations. This sets the bones' local SRT.
        skeleton.updateWorldTransform(); // Uses the bones' local SRT to compute their world SRT.

        // Configure the camera, SpriteBatch, and SkeletonRendererDebug.
        camera.update();

        batch.getProjectionMatrix().set(camera.combined);
        debugRenderer.getShapeRenderer().setProjectionMatrix(camera.combined);

        batch.begin();
        renderer.draw(batch, skeleton); // Draw the manSkeleton images.
        batch.end();

        debugRenderer.draw(skeleton); // Draw debug lines.
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
