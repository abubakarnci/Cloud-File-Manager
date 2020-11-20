package com.example.cloudfilemanager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    Button btnlogout;
    ImageButton imageButton;
    ImageButton songButton;
    ImageButton fileButton;

    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        btnlogout=findViewById(R.id.logout);
        imageButton=findViewById(R.id.imageButton);
        songButton=findViewById(R.id.songButton);
        fileButton=findViewById(R.id.documentsButton);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent inTOImage =new Intent(HomeActivity.this,ImageActivity.class);
                startActivity(inTOImage);
            }
        });

        songButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent inTOSong =new Intent(HomeActivity.this,SongActivity.class);
                startActivity(inTOSong);
            }
        });

        fileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent inTOFile =new Intent(HomeActivity.this,FileActivity.class);
                startActivity(inTOFile);
            }
        });

        btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent inToMain=new Intent(HomeActivity.this,MainActivity.class);
                startActivity(inToMain);
            }
        });

    }
}