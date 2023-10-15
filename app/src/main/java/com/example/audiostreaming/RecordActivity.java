package com.example.audiostreaming;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.audiostreaming.Model.Music;
import com.example.audiostreaming.Service.RecorderService;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class RecordActivity extends AppCompatActivity implements View.OnClickListener{

    private YouTubePlayerView youTubePlayerView;
    private YouTubePlayerTracker tracker;
    private TextView tvLyric, tvName, countdownTextView;
    private Music music;
    private View vBack;
    private ImageView ivVolume, ivMic;
    private boolean recording = false;
    private ActivityResultLauncher<Intent> fileBrowserLauncher = null;
    private CountDownTimer countDownTimer;
    private Dialog dialog;
    private int count;
    private ArrayList<Music> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        initView();
        Intent intent = getIntent();
        //music = (Music) intent.getSerializableExtra("music");
        int posi = intent.getIntExtra("posi", -1);
        list = intent.getParcelableArrayListExtra("listMusic");
        music = list.get(posi);

        tvName.setText(music.getName());

        tvLyric.setText(music.getLyrics());
        vBack.setOnClickListener(this);
        ivMic.setOnClickListener(this);
        setVideo();

        setStartStop();
    }

    private void setStartStop() {
        fileBrowserLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            // Handle the return of the save as dialog.
            if (result.getResultCode() == android.app.Activity.RESULT_OK) {
                Intent resultData = result.getData();
                if (resultData != null) {
                    Uri u = resultData.getData();
                    try {
                        ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(u, "w");
                        if (pfd != null) {
                            //Create Dialog
                            count = 3;
                            Intent serviceIntent = new Intent(this, RecorderService.class);
                            countDownTimer = new CountDownTimer(4000, 1000) {
                                @Override
                                public void onTick(long l) {
                                    if(count != 0)
                                        countdownTextView.setText(String.valueOf(count));
                                    else
                                        countdownTextView.setText("Bắt\nĐầu\nGhi\nÂm!");
                                    dialog.show();
                                    count--;
                                }

                                @Override
                                public void onFinish() {
                                    serviceIntent.putExtra("fileDescriptor", pfd.detachFd());
                                    ContextCompat.startForegroundService(RecordActivity.this, serviceIntent);
                                    recording = true;
                                    updateButton();
                                    dismissDialog();
                                }
                            };
                            countDownTimer.start();
                        } else Log.d("Recorder", "File descriptor is null.");
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        // Checking permissions.
        String[] permissions = {
                Manifest.permission.RECORD_AUDIO
        };
        for (String s:permissions) {
            if (ContextCompat.checkSelfPermission(this, s) != PackageManager.PERMISSION_GRANTED) {
                // Some permissions are not granted, ask the user.
                ActivityCompat.requestPermissions(this, permissions, 0);
                return;
            }
        }

        // Got all permissions, show button.
        showButton();
    }

    private void initView() {
        youTubePlayerView = findViewById(R.id.youtubePlayerView);
        tvLyric = findViewById(R.id.tvLyric);
        tvName = findViewById(R.id.tvName);
        vBack = findViewById(R.id.vBack);
        ivMic = findViewById(R.id.ivMic);
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.setContentView(R.layout.customer_dialog);
        countdownTextView = dialog.findViewById(R.id.countdownTextView);
    }

    private void setVideo() {
        getLifecycle().addObserver(youTubePlayerView);
        tracker = new YouTubePlayerTracker();
        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                youTubePlayer.cueVideo(music.getVideo(), 0);
                youTubePlayer.addListener(tracker);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Called when the user answers to the permission dialogs.
        if ((requestCode != 0) || (grantResults.length < 1) || (grantResults.length != permissions.length)) return;
        boolean hasAllPermissions = true;

        for (int grantResult:grantResults) if (grantResult != PackageManager.PERMISSION_GRANTED) {
            hasAllPermissions = false;
            Toast.makeText(getApplicationContext(), "Please allow all permissions for the app.", Toast.LENGTH_LONG).show();
        }

        if (hasAllPermissions) showButton();
    }

    private void showButton() {
        ivMic.setVisibility(View.VISIBLE);
    }

    private void updateButton() {
        if(recording)
            ivMic.setImageResource(R.drawable.icon_mic_24);
        else
            ivMic.setImageResource(R.drawable.icon_mic_off_24);
    }

    private void dismissDialog(){
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    public void onClick(View view) {

        if(view == vBack){
            Intent intent = new Intent(RecordActivity.this, MainActivity.class);
            startActivity(intent);
        }

        if(view == ivMic){
            if (recording) {
                Intent serviceIntent = new Intent(this, RecorderService.class);
                serviceIntent.setAction("stop");
                startService(serviceIntent);
                recording = false;
                updateButton();
            } else {
                // Open the file browser to pick a destination.
                android.content.Intent intent = new android.content.Intent(Intent.ACTION_CREATE_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("application/octet-stream");
                String fileName = music.getName() + ".wav";
                intent.putExtra(Intent.EXTRA_TITLE, fileName);
                fileBrowserLauncher.launch(intent);
            }
        }

    }
}