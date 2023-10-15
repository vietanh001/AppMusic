package com.example.audiostreaming;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.audiostreaming.Model.Music;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class PlayMusicActivity extends AppCompatActivity {

    private ImageButton ibNext, ibStop, ibPrevious, ibPlay;
    private TextView tvName, tvTimeCurrent, tvTimeEnd;
    private SeekBar seekBar;
    private CircleImageView picture;
    private Animation animation;
    private int posi;
    private Music music;
    private ArrayList<Music> list;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);
        initView();

        Intent intent = getIntent();
        posi = (int)intent.getIntExtra("posi", -1);
        list = intent.getParcelableArrayListExtra("listMusic");

        music = list.get(posi);
        tvName.setText(music.getName());

        mediaPlayer = MediaPlayer.create(PlayMusicActivity.this,
                Uri.parse(music.getImage()));
        mediaPlayer.start();
        
        setTimeEnd();
        UpdateTime();
    }

    private void initView() {
        ibNext = findViewById(R.id.ibNext);
        ibStop = findViewById(R.id.ibStop);
        ibPrevious = findViewById(R.id.ibPrevious);
        ibPlay = findViewById(R.id.ibPlay);
        tvName = findViewById(R.id.tvName);
        tvTimeCurrent = findViewById(R.id.tvTimeCurrent);
        tvTimeEnd = findViewById(R.id.tvTimeEnd);
        seekBar = findViewById(R.id.SeekBar);
        picture = findViewById(R.id.picture);
    }

    private void setTimeEnd() {
        SimpleDateFormat timetotal = new SimpleDateFormat("mm:ss");
        tvTimeEnd.setText(timetotal.format(mediaPlayer.getDuration()));
        seekBar.setMax(mediaPlayer.getDuration());
    }

    private void UpdateTime() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat timecurr = new SimpleDateFormat("mm:ss");
                tvTimeCurrent.setText(timecurr.format(mediaPlayer.getCurrentPosition()));
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        posi = posi +1;
                        if(posi > list.size()-1)
                        {
                            posi = 0;
                        }
                        mediaPlayer = MediaPlayer.create(PlayMusicActivity.this, Uri.parse(list.get(posi).getImage()));
                        mediaPlayer.start();
                        ibPlay.setImageResource(R.drawable.icon_pause_24);
                        tvName.setText(list.get(posi).getName());
                        setTimeEnd();
                        UpdateTime();
                        //picture.startAnimation(animation);
                    }
                });
                handler.postDelayed(this::run, 500);
            }
        }, 100);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        ibPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying())
                {
                    mediaPlayer.pause();
                    ibPlay.setImageResource(R.drawable.icon_play_arrow_24);
                }
                else
                {
                    mediaPlayer.start();
                    ibPlay.setImageResource(R.drawable.icon_pause_24);
                }
                setTimeEnd();
                UpdateTime();
                //picture.startAnimation(animation);
            }
        });

        ibNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                posi = posi + 1;
                if(posi > list.size() -1)
                    posi = 0;
                mediaPlayer.stop();
                mediaPlayer
                        = MediaPlayer.create(PlayMusicActivity.this, Uri.parse(list.get(posi).getImage()));
                mediaPlayer.start();
                ibPlay.setImageResource(R.drawable.icon_pause_24);
                tvName.setText(list.get(posi).getName());
                setTimeEnd();
                UpdateTime();
                //imgHinh.startAnimation(animation);
            }
        });

        ibPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                posi = posi - 1;
                if(posi < 0)
                    posi = list.size() - 1;
                mediaPlayer.stop();
                mediaPlayer
                        = MediaPlayer.create(PlayMusicActivity.this, Uri.parse(list.get(posi).getImage()));
                mediaPlayer.start();
                ibPlay.setImageResource(R.drawable.icon_pause_24);
                tvName.setText(list.get(posi).getName());
                setTimeEnd();
                UpdateTime();
                //imgHinh.startAnimation(animation);
            }
        });

        ibStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                ibPlay.setImageResource(R.drawable.icon_play_arrow_24);
                mediaPlayer
                        = MediaPlayer.create(PlayMusicActivity.this, Uri.parse(list.get(posi).getImage()));
                tvName.setText(list.get(posi).getName());
                setTimeEnd();
                UpdateTime();
                //imgHinh.startAnimation(animation);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}