package com.example.audiostreaming.Fragment;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.audiostreaming.Adapter.ListMusicAdapter;
import com.example.audiostreaming.Model.Music;
import com.example.audiostreaming.PlayMusicActivity;
import com.example.audiostreaming.R;

import java.util.ArrayList;

public class FragmentMusic extends Fragment {

    private int MY_PERMISSIONS_REQUEST_READ_MEDIA_AUDIO = 1;
    private ArrayList<Music> list;
    private ListMusicAdapter adapter;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_music, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerView);
        list = new ArrayList<>();
        getMusicList();
    }

    @Override
    public void onResume() {
        super.onResume();
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, true);
        recyclerView.setLayoutManager(manager);

        adapter = new ListMusicAdapter(getContext(), list);
        recyclerView.setAdapter(adapter);

        adapter.setItemListener(new ListMusicAdapter.ItemListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getContext(), PlayMusicActivity.class);
                intent.putExtra("posi", position);
                intent.putParcelableArrayListExtra("listMusic", (ArrayList<? extends Parcelable>) list);
                startActivity(intent);
            }
        });
    }

    private void getMusicList() {
        ContentResolver contentResolver = getContext().getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = contentResolver.query(musicUri, null, null, null, null);

        if(musicCursor != null && musicCursor.moveToFirst())
        {
            int name = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int id = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int artist = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);


            do {
                int currentID = musicCursor.getInt(id);
                String currentName = musicCursor.getString(name);
                String currentArtist = musicCursor.getString(artist);
                Uri uriSong = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        musicCursor.getInt(id));
                list.add(new Music(String.valueOf(currentID), currentName, uriSong.toString(), "", "", currentArtist, ""));
            }
            while(musicCursor.moveToNext());

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == MY_PERMISSIONS_REQUEST_READ_MEDIA_AUDIO){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getMusicList();
            }else{
                Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}