package com.example.audiostreaming;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.audiostreaming.Model.Music;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.Calendar;
import java.util.Locale;

public class AddMusicActivity extends AppCompatActivity implements View.OnClickListener {

    private View vBack;
    private EditText etName, etLinkYoutube, etAuthor, etLyric;
    private ImageView ivImage;
    private TextView tvTime;
    private Button btCheck, btAddMusic;
    private YouTubePlayerView youTubePlayView;
    private String videoId;
    private Uri imageUri;
    private DatabaseReference root_music = FirebaseDatabase.getInstance().getReference("Music");
    private StorageReference storage = FirebaseStorage.getInstance().getReference();
    private String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_music);
        initView();

        ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 2);
            }
        });

        tvTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int hours = calendar.get(Calendar.HOUR);
                int minute = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddMusicActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                String selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                                tvTime.setText(selectedTime);
                            }
                        }, hours, minute, false);
                timePickerDialog.show();
            }
        });
        vBack.setOnClickListener(this);
        btCheck.setOnClickListener(this);
        btAddMusic.setOnClickListener(this);
    }

    private void initView() {
        vBack = findViewById(R.id.vBack);
        etName = findViewById(R.id.etName);
        etLinkYoutube = findViewById(R.id.etLinkYoutube);
        etAuthor = findViewById(R.id.etAuthor);
        etLyric = findViewById(R.id.etLyric);
        ivImage = findViewById(R.id.ivImage);
        tvTime = findViewById(R.id.tvTime);
        youTubePlayView = findViewById(R.id.youtubePlayerView);
        btCheck = findViewById(R.id.btCheck);
        btAddMusic = findViewById(R.id.btAddMusic);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            ivImage.setImageURI(imageUri);
        }
    }

    private void AddMusic(Uri uriImage) {
        String name = etName.getText().toString();
        String link = etLinkYoutube.getText().toString();
        String time = tvTime.getText().toString();
        String author = etAuthor.getText().toString();
        String lyric = etLyric.getText().toString();
        videoId = link;
        int n = videoId.length();
        String video = videoId.substring(n - 11, n);

        root_music.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String[] list = name.split("\\s");
                key = "";
                for (String w : list)
                    key = key + w.toUpperCase().substring(0, 1);
                StorageReference fileRef = storage.child(key);
                fileRef.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String image = uri.toString();
                                Music music = new Music(key, name, image, video, time, author, lyric);
                                root_music.child(key).setValue(music);
                                Toast.makeText(AddMusicActivity.this, "Thêm bài thành công!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(AddMusicActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        });
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == btCheck) {
            String newVideoId = etLinkYoutube.getText().toString();
            if (!newVideoId.isEmpty()) {
                videoId = newVideoId;
            } else {
                Toast.makeText(AddMusicActivity.this, "Không để trống link Youtube", Toast.LENGTH_SHORT).show();
                return;
            }
            youTubePlayView.getYouTubePlayerWhenReady(new YouTubePlayerCallback() {
                @Override
                public void onYouTubePlayer(@NonNull YouTubePlayer youTubePlayer) {
                    int n = videoId.length();
                    youTubePlayer.cueVideo(videoId.substring(n - 11, n), 0);
                }
            });
        }

        if (view == btAddMusic) {
            if (imageUri != null) {
                AddMusic(imageUri);
            } else {
                Toast.makeText(AddMusicActivity.this, "Chọn ảnh tải lên!", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if(view == vBack){
            Intent intent = new Intent(AddMusicActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
}