package com.example.cloudfilemanager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

public class FileActivity extends AppCompatActivity {

    //Declaring variables, buttons etc
    private static final int PICKFILE=1;
    private Button choose_file;
    private Button upload;
    private Button showUploads;
    private EditText fileName;
    private ImageView imageView;
    private ProgressBar progressBar;

    private Uri mFileUri;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);

        //initializing
        choose_file=findViewById(R.id.choose_file);
        upload=findViewById(R.id.upload);
        showUploads=findViewById(R.id.showUploads);
        fileName=findViewById(R.id.fileName);
        imageView=findViewById(R.id.imageView);
        progressBar=findViewById(R.id.progressBar);

        //settingup path for files in firebase storage and database
        mStorageRef= FirebaseStorage.getInstance().getReference("Files");
        mDatabaseRef= FirebaseDatabase.getInstance().getReference("Files");


        // Button click listeners for choosing, uploading, display uploaded files
        choose_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mUploadTask !=null && mUploadTask.isInProgress()){
                    Toast.makeText(FileActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                }
                else {
                    uploadFiles();
                }
            }
        });
        showUploads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openFileActivity();
            }
        });
    }
    //accessing zip files from the phone internal storage through file chooser
    private void openFileChooser(){
        Intent intent= new Intent();
        intent.setType("application/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICKFILE);
    }


    //getting its Uri path reference for accessing it
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if(requestCode== PICKFILE && resultCode== RESULT_OK && data!=null && data.getData()!=null){
            mFileUri=data.getData();

            }
    }

    // storing the extension of file
    private String getFileExtension(Uri uri){
        ContentResolver cR= getContentResolver();
        MimeTypeMap mime= MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    //here uploading the selected file to the cloud
    private void uploadFiles(){
        if(mFileUri!=null){
            //storage ref path
            StorageReference fileReference =mStorageRef.child(System.currentTimeMillis() +"." +getFileExtension(mFileUri));
            mUploadTask=fileReference.putFile(mFileUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //String downloadUrl = taskSnapshot.getStorage().getDownloadUrl().toString();

                            final Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                            firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String downloadUrl = uri.toString();

                                    // progress bar to display the uploading status
                                    Handler handler =new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressBar.setProgress(0);
                                        }
                                    }, 500);
                                    //toast when uploading will be successful
                                    Toast.makeText(FileActivity.this,"Upload Successful", Toast.LENGTH_LONG).show();
                                    UploadFiles upload= new UploadFiles(fileName.getText().toString().trim(),
                                            downloadUrl);
                                    //taskSnapshot.getStorage().getDownloadUrl().toString()
                                    // Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                                    String uploadId = mDatabaseRef.push().getKey();
                                    mDatabaseRef.child(uploadId).setValue(upload);
                                }
                            });




//in case of failure
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(FileActivity.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot tasksnapshot) {
                            // progressbar bytes calculation
                            double progress =(100.0 * tasksnapshot.getBytesTransferred()/tasksnapshot.getTotalByteCount());

                            progressBar.setProgress((int) progress);
                        }
                    });
        }
        else{
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    //moving to next activity
    private void openFileActivity(){
        Intent intent=new Intent(this, FileActivity2.class);
        startActivity(intent);
    }



}