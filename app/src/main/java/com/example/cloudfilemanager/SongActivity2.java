package com.example.cloudfilemanager;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SongActivity2 extends AppCompatActivity implements SongsAdapter.onItemClickListener{


    RecyclerView recyclerView;
    ProgressBar progressBar;
    ArrayList<UploadSong> mUploads;
    FirebaseStorage mStorage;
    DatabaseReference databaseReference;
    ValueEventListener valueEventListener;
    MediaPlayer mediaPlayer;
    SongsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song2);

        recyclerView=findViewById(R.id.recyclerView);
        progressBar=findViewById(R.id.progressBar);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUploads=new ArrayList<>();
        adapter=new SongsAdapter(SongActivity2.this, mUploads);
        recyclerView.setAdapter(adapter);
        adapter.setonItemClickListener(SongActivity2.this);



        mStorage=FirebaseStorage.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference("Songs");
        valueEventListener=databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot) {
                mUploads.clear();
                for(DataSnapshot dss : dataSnapshot.getChildren()){
                    UploadSong uploadSong = dss.getValue(UploadSong.class);
                    uploadSong.setKey(dss.getKey());
                    mUploads.add(uploadSong);
                }

               adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(getApplicationContext(),""+ error.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(valueEventListener);
    }

    public void playSong(List<UploadSong> arrayListSongs, int adapterPosition) throws IOException {
        UploadSong uploadSong=arrayListSongs.get(adapterPosition);
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
        }

        mediaPlayer= new MediaPlayer();
        mediaPlayer.setDataSource(uploadSong.getSongUrl());
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {

                mediaPlayer.start();

            }
        });
        mediaPlayer.prepareAsync();
    }


    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "Normal click at position: " + position+ "\n Hold for more options", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onWhatEverClick(int position) {
        Toast.makeText(this, "Whatever click at position: " + position, Toast.LENGTH_SHORT).show();

    }
    public void onDownloadClick(int position){
        UploadSong p=mUploads.get(position);
        Intent i=new Intent(Intent.ACTION_VIEW);
        i.setType("audio/*");
        i.setData(Uri.parse(p.getSongUrl()));
        startActivity(i);
    }
    @Override
    public void onDeleteClick(int position) {

        UploadSong selectedItem = mUploads.get(position);
        final String selectedKey = selectedItem.getKey();
        StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getSongUrl());
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                databaseReference.child(selectedKey).removeValue();
                Toast.makeText(SongActivity2.this, "Item deleted", Toast.LENGTH_SHORT).show();
            }
        });

    }




}