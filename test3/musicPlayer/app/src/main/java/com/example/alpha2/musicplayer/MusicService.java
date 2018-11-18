package com.example.alpha2.musicplayer;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.animation.LinearInterpolator;

import java.io.File;
import java.util.List;


public class MusicService extends Service {
    public final IBinder binder = new MyBinder();
    public class MyBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    public static int isReturnTo = 0;
    public static MediaPlayer musPlay = new MediaPlayer();
    public static ObjectAnimator animator;
    public MusicService() {
        initmusPlay();

    }

    public void initmusPlay() {
        try {

            String file_path = "/storage/self/primary/47.mp3";

            musPlay.setDataSource(file_path);
            musPlay.prepare();
            musPlay.setLooping(true);  // 设置循环播放
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("WrongConstant")
    public  void AnimatorAction() {
        if (musPlay.isPlaying()) {
            animator.setDuration(3000);
            animator.setInterpolator(new LinearInterpolator()); // 均速旋转
            animator.setRepeatCount(ValueAnimator.INFINITE); // 无限循环
            animator.setRepeatMode(ValueAnimator.INFINITE);
            animator.start();
        }
    }
    private int flag = -1;//播放状态记录，-1暂停，1为播放
    public static String which = "";
    public void playOrPause() {
        flag=-(flag);
        which = "pause";
        //首次播放开始旋转，恢复播放从初始旋转
        if(musPlay.isPlaying()){
            musPlay.pause();
            animator.pause();
        } else {
            musPlay.start();

            if ((flag == 1) || (isReturnTo == 1)) {
                animator.setDuration(3000);
                animator.setInterpolator(new LinearInterpolator()); // 均速旋转
                animator.setRepeatCount(ValueAnimator.INFINITE); // 无限循环
                animator.setRepeatMode(ValueAnimator.INFINITE);
                animator.start();
            } else {
                animator.resume();
            }
        }
    }
    public void stop() {
        which = "stop";
        animator.pause();
        if(musPlay != null) {
            musPlay.pause();
            musPlay.stop();
            try {
                musPlay.prepare();
                musPlay.seekTo(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onDestroy() {
        musPlay.stop();
        musPlay.release();
        super.onDestroy();
    }
    /**
     * onBind 是 Service 的虚方法，因此我们不得不实现它。
     * 返回 null，表示客服端不能建立到此服务的连接。
     */
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

}
