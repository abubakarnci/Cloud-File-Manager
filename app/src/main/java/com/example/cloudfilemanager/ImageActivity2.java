package com.example.cloudfilemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ImageActivity2 extends AppCompatActivity implements ImageAdapter.onItemClickListener {

    //Declaring variables, RecyclerView etc
    private RecyclerView mRecyclerView;
    private ImageAdapter mAdapter;
    private ProgressBar mProgressCircle;
    private FirebaseStorage mStorage;
    private DatabaseReference mDatabaseRef;
    private ValueEventListener mDBListener;
    private ArrayList<UploadImage> mUploads;
    Animation animation;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image2);

        //initializing
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mProgressCircle = findViewById(R.id.progress_circle);

        mUploads = new ArrayList<>();
        mAdapter = new ImageAdapter(ImageActivity2.this, mUploads);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setonItemClickListener(ImageActivity2.this);

        mStorage=FirebaseStorage.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Images");

       mDBListener= mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override

            //setting properties of image
            public void onDataChange( DataSnapshot dataSnapshot) {

                mUploads.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    UploadImage upload = postSnapshot.getValue(UploadImage.class);
                    upload.setKey(postSnapshot.getKey());
                    mUploads.add(upload);
                }
              mAdapter.notifyDataSetChanged();
                mProgressCircle.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(ImageActivity2.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressCircle.setVisibility(View.INVISIBLE);
            }
        });
    }

    // activity options when click on some image
    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "Normal click at position: " + position+ "\n Hold for more options", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onWhatEverClick(int position) {
        Toast.makeText(this, "Whatever click at position: " + position, Toast.LENGTH_SHORT).show();

    }
    public void onDownloadClick(int position){
        UploadImage p=mUploads.get(position);
        Intent i=new Intent(Intent.ACTION_VIEW);
        i.setType("image/*");
        i.setData(Uri.parse(p.getImageUrl()));
        startActivity(i);
    }
    public void onRotateClick(){
        animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate);
        mRecyclerView.startAnimation(animation);

    }

    @Override
    public void onDeleteClick(int position) {

        UploadImage selectedItem = mUploads.get(position);
        final String selectedKey = selectedItem.getKey();
        StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getImageUrl());
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mDatabaseRef.child(selectedKey).removeValue();
                Toast.makeText(ImageActivity2.this, "Item deleted", Toast.LENGTH_SHORT).show();
            }
        });

    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabaseRef.removeEventListener(mDBListener);
    }
}