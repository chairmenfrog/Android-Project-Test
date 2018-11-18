package com.example.alpha2.musicplayer;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;


import java.text.SimpleDateFormat;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button isPlay;
    private Button stop;
    private Button quit;

    private ImageView coverImage;
    // private ObjectAnimator animator;

    private TextView totalTime;
    private TextView playingTime;
    private TextView stateText;

    private SeekBar seekBar;
    private TextView pathText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindServiceConnection();
        musicService = new MusicService();

        coverImage = (ImageView) findViewById(R.id.coverImage);
        musicService.animator = ObjectAnimator.ofFloat(coverImage, "rotation", 0, 359);

        isPlay = (Button) findViewById(R.id.isPlayButton);
        isPlay.setOnClickListener(new my_listener());

        stop = (Button) findViewById(R.id.stopButton);
        stop.setOnClickListener(new my_listener());

        quit = (Button) findViewById(R.id.quitButton);
        quit.setOnClickListener(new my_listener());

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setProgress(musicService.musPlay.getCurrentPosition());
        seekBar.setMax(musicService.musPlay.getDuration());

        totalTime = (TextView) findViewById(R.id.totalTime);
        playingTime = (TextView) findViewById(R.id.playingTime);
        stateText = (TextView) findViewById(R.id.stateText);

        pathText = (TextView) findViewById(R.id.pathText);
        String sdcard = "音乐文件的路径为：/storage/self/primary/47.mp3";
        //+ Environment.getExternalStorageDirectory().getAbsolutePath().toString()+
        pathText.setText(sdcard);

    }

    private MusicService musicService;
    private SimpleDateFormat time = new SimpleDateFormat("mm:ss");
    private ServiceConnection serBind = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            musicService = ((MusicService.MyBinder) iBinder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            musicService = null;
        }
    };

    private void bindServiceConnection() {
        Intent intent = new Intent(this, MusicService.class);
        startService(intent);
        bindService(intent, serBind, this.BIND_AUTO_CREATE);
    }

    public Handler handler = new Handler();
    public Runnable runnable = new Runnable() {
        //修改播放时间，加入消息队列
        public void run() {

            isPlay.setOnClickListener(new my_listener());
            stop.setOnClickListener(new my_listener());
            quit.setOnClickListener(new my_listener());

            if(musicService.musPlay.isPlaying()) {
                stateText.setText("Playing");
            } else {
                if (musicService.which.equals("stop"))  {
                    stateText.setText("Stop");
                } else if (musicService.which.equals("pause")){
                    stateText.setText("Pause");
                }
            }
            //设置播放时间
            playingTime.setText(time.format(musicService.musPlay.getCurrentPosition()));
            totalTime.setText(time.format(musicService.musPlay.getDuration()));
            seekBar.setProgress(musicService.musPlay.getCurrentPosition());
            seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        musicService.musPlay.seekTo(seekBar.getProgress());
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            handler.postDelayed(runnable, 100);
        }
    };

    @Override
    public void onPause(){
        super.onPause();
        if(brought_background()) {
            musicService.isReturnTo = 1;
            Log.e("b","后台中");
        }
    }

    @Override
    public void onRestart() {
        super.onRestart();
        musicService.isReturnTo = 1;
    }

    @Override
    protected void onResume() {

        musicService.AnimatorAction();
        verify_storage(this);

        if(musicService.musPlay.isPlaying()) {
            stateText.setText("Playing");
        } else {
            if (musicService.which.equals("stop"))  {
                stateText.setText("Stop");
            } else if (musicService.which.equals("pause")){
                stateText.setText("Pause");
            }
        }
        seekBar.setProgress(musicService.musPlay.getCurrentPosition());
        seekBar.setMax(musicService.musPlay.getDuration());
        handler.post(runnable);
        super.onResume();
        Log.d("hint", "handler post runnable");
    }

    private class my_listener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.isPlayButton:
                    changePlay();
                    musicService.playOrPause();
                    break;
                case R.id.stopButton:
                    musicService.stop();
                    changeStop();
                    break;
                case R.id.quitButton:
                    quit();
                    break;
                default:
                    break;
            }
        }
    }

    private void changePlay() {

        if(musicService.musPlay.isPlaying()){
            stateText.setText("Pause");
            isPlay.setText("PLAY");
            //animator.pause();
        } else {
            stateText.setText("Playing");
            isPlay.setText("PAUSE");

        }
    }

    private void changeStop() {
        stateText.setText("Stop");
        seekBar.setProgress(0);
        //animator.pause();
    }

    private void quit() {
        musicService.animator.end();
        handler.removeCallbacks(runnable);
        unbindService(serBind);
        try {
            finish();
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }
    @Override
    public void onDestroy() {
        unbindService(serBind);
        super.onDestroy();
    }


    private boolean brought_background() {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        //判断task栈顶的活动是否为服务
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(getPackageName())) {
                return true;
            }
        }
        return false;
    }

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    //判断读写存储权限，没有权限时向用户请求权限
    public static void verify_storage(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
