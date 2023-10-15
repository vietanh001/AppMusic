package com.example.audiostreaming.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.audiostreaming.Adapter.ListMusicAdapter;
import com.example.audiostreaming.AddMusicActivity;
import com.example.audiostreaming.RecordActivity;
import com.example.audiostreaming.Model.Music;
import com.example.audiostreaming.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FragmentHome extends Fragment implements View.OnClickListener{

    private Button btAdd;
    private DatabaseReference root_music = FirebaseDatabase.getInstance().getReference("Music");
    private ArrayList<Music> list;
    private ListMusicAdapter adapter;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);

        LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, true);
        recyclerView.setLayoutManager(manager);
        list = new ArrayList<>();
        adapter = new ListMusicAdapter(getContext(), list);
        recyclerView.setAdapter(adapter);

        root_music.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Music music = dataSnapshot.getValue(Music.class);
                    list.add(music);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        adapter.setItemListener(new ListMusicAdapter.ItemListener() {
            @Override
            public void onItemClick(View view, int position) {
                Music music = adapter.getMusic(position);
                Intent intent = new Intent(getContext(), RecordActivity.class);
                //intent.putExtra("music", music);
                intent.putExtra("posi", position);
                intent.putParcelableArrayListExtra("listMusic", (ArrayList<? extends Parcelable>) list);
                startActivity(intent);
            }
        });

        btAdd.setOnClickListener(this);
    }

    private void initView(View view) {
        btAdd = view.findViewById(R.id.btAdd);
        recyclerView = view.findViewById(R.id.recyclerView);
    }

    @Override
    public void onClick(View view) {
        if(view == btAdd){
            Intent intent = new Intent(getContext(), AddMusicActivity.class);
            startActivity(intent);
        }
    }
}