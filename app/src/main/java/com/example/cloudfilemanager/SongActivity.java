package com.example.cloudfilemanager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SongActivity extends AppCompatActivity {


    private static final int PICKSONG=101;
    private Button choose_song;
    private Button upload;
    private Button showUploads;
    private EditText fileName;
    private TextView textView;
    private ProgressBar progressBar;

    private Uri mSongUri;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);

        choose_song=findViewById(R.id.choose_song);
        upload=findViewById(R.id.upload);
        showUploads=findViewById(R.id.showUploads);
        fileName=findViewById(R.id.fileName);
        textView=findViewById(R.id.textView);
        progressBar=findViewById(R.id.progressBar);

        mStorageRef= FirebaseStorage.getInstance().getReference("Songs");
        mDatabaseRef= FirebaseDatabase.getInstance().getReference("Songs");


        choose_song.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mUploadTask !=null && mUploadTask.isInProgress()){
                    Toast.makeText(SongActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                }
                else {
                    uploadFile();
                }
            }
        });
        showUploads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openSongsActivity();
            }
        });

    }

    private void openFileChooser(){
        Intent intent= new Intent();
        intent.setType("audio/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICKSONG);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== PICKSONG && resultCode== RESULT_OK && data.getData()!=null){
            mSongUri=data.getData();
            String fileName=getFileName(mSongUri);
            textView.setText(fileName);
        }
    }

    private String getFileName(Uri uri) {
        String result= null;
        if(uri.getScheme().equals("contect")){

            Cursor cursor=getContentResolver().query(uri,null,null,null,null);
            try{
                if(cursor!=null && cursor.moveToFirst()){
                    result=cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));

                }
            }
            finally {
                cursor.close();
            }
        }

        if(result==null){
            result=uri.getPath();
            int cut=result.lastIndexOf('/');
            if(cut!=-1){
                result=result.substring(cut +1);

            }
        }
        return result;
    }

    private String getFileExtension(Uri uri){
        ContentResolver cR= getContentResolver();
        MimeTypeMap mime= MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile(){
        if(mSongUri!=null){
            String durationTxt;
            StorageReference fileReference =mStorageRef.child(System.currentTimeMillis() +"." +getFileExtension(mSongUri));
            int durationInMills=findSongDuration(mSongUri);
            if(durationInMills==0){
                durationTxt="NA";
            }
            durationTxt=getDurationFromMilli(durationInMills);
            final String finalDurationTxt = durationTxt;
            mUploadTask=fileReference.putFile(mSongUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //String downloadUrl = taskSnapshot.getStorage().getDownloadUrl().toString();

                            final Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                            firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String downloadUrl = uri.toString();
                                    // complete the rest of your code


                                    Handler handler =new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressBar.setProgress(0);
                                        }
                                    }, 500);
                                    Toast.makeText(SongActivity.this,"Upload Successful", Toast.LENGTH_LONG).show();
                                    UploadSong upload= new UploadSong(fileName.getText().toString(), finalDurationTxt,
                                            downloadUrl);
                                    //taskSnapshot.getStorage().getDownloadUrl().toString()
                                    // Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                                    String uploadId = mDatabaseRef.push().getKey();
                                    mDatabaseRef.child(uploadId).setValue(upload);
                                }
                            });





                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SongActivity.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot tasksnapshot) {
                            double progress =(100.0 * tasksnapshot.getBytesTransferred()/tasksnapshot.getTotalByteCount());

                            progressBar.setProgress((int) progress);
                        }
                    });
        }
        else{
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private String getDurationFromMilli(int durationInMills) {
        Date date=new Date(durationInMills);
        SimpleDateFormat simple=new SimpleDateFormat("mm:ss", Locale.getDefault());
        String myTime=simple.format(date);
        return myTime;
    }

    private int findSongDuration(Uri mSongUri) {
        int timeInMillisec=0;
        try{
            MediaMetadataRetriever retriever=new MediaMetadataRetriever();
            retriever.setDataSource(this,mSongUri);
            String time=retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            timeInMillisec= Integer.parseInt(time);
            retriever.release();
            return timeInMillisec;
        }
        catch(Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    private void openSongsActivity(){
        Intent intent=new Intent(this, SongActivity2.class);
        startActivity(intent);
    }
}