package com.example.cloudfilemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    EditText emailId, passwordId, name,cPassword;
    Button btnSignUp;
    TextView tvSignIn;
    FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //setting up firebase connection
        mFirebaseAuth=FirebaseAuth.getInstance();
        emailId=findViewById(R.id.emailId);
        passwordId=findViewById(R.id.passwordId);
        cPassword=findViewById(R.id.cPassword);
        name=findViewById(R.id.name);
        btnSignUp=findViewById(R.id.btnSignIn);
        tvSignIn=findViewById(R.id.tvSignUp);



        btnSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                String email=emailId.getText().toString();
                String pwd=passwordId.getText().toString();
                String name1=name.getText().toString();
                String cPwd=cPassword.getText().toString();
                    //if user left any textfield empty
                    //focusing on empty textfield
                if(email.isEmpty()){
                    emailId.setError("Please Enter Email id");
                    emailId.requestFocus();
                }
               else if(pwd.isEmpty()){
                    passwordId.setError("Please Enter Password");
                    passwordId.requestFocus();
                }
               else if(cPwd.isEmpty()){
                    cPassword.setError("Please Confirm Password");
                    cPassword.requestFocus();
                }
               else if(name1.isEmpty()){
                    name.setError("Please Enter Name");
                    name.requestFocus();
                }
               else if(email.isEmpty() && pwd.isEmpty() && cPwd.isEmpty() && name1.isEmpty()){
                    Toast.makeText(MainActivity.this,"Fields are Empty",Toast.LENGTH_SHORT).show();

                }
               else if(!(pwd.equals(cPwd))){
                    Toast.makeText(MainActivity.this,"Passwords are not same, Please Check!!!",Toast.LENGTH_SHORT).show();

                }
               else if(!(email.isEmpty() && pwd.isEmpty() && cPwd.isEmpty() && name1.isEmpty())){
                   mFirebaseAuth.createUserWithEmailAndPassword(email,pwd).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                       @Override
                       public void onComplete(@NonNull Task<AuthResult> task) {
                           if(!task.isSuccessful()){
                               Toast.makeText(MainActivity.this,"SignUp Unsuccessful, Please Try Again",Toast.LENGTH_SHORT).show();

                           }
                           //successful sign up
                           else{
                               startActivity(new Intent(MainActivity.this,HomeActivity.class));
                           }
                       }
                   });
                }
               else{
                    Toast.makeText(MainActivity.this,"Error Ocurred!!",Toast.LENGTH_SHORT).show();

                }
            }
        });

        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(MainActivity.this,LoginActivity.class);
                startActivity(i);
            }
        });

    }
}