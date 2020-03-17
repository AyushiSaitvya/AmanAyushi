package com.example.chitchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {


    private Button regbutton;
    private EditText mEmail;
    private EditText mPassword;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private static final String LOG_TAG = "MyActivity";
    private String email, password;
    private DatabaseReference rootref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        regbutton=findViewById(R.id.create_Account);
        mEmail=findViewById(R.id.input_email);
        mPassword=findViewById(R.id.input_password);
        progressBar=findViewById(R.id.progressBar);
        rootref=FirebaseDatabase.getInstance().getReference();

       mAuth=FirebaseAuth.getInstance();
        regbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerNewUser();
            }
        });
    }
    private void registerNewUser() {
        progressBar.setVisibility(View.VISIBLE);


        email = mEmail.getText().toString();
        password = mPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Please enter email...", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Please enter password!", Toast.LENGTH_LONG).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            String User_Id=mAuth.getCurrentUser().getUid();
                            rootref.child("users").child(User_Id).setValue("");
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(LOG_TAG, "createUserWithEmail:success");
                            Toast.makeText(RegisterActivity.this, "USER CREATED SUCCESSFULLY",
                                    Toast.LENGTH_LONG).show();
                           Intent intent=new Intent(RegisterActivity.this,SettingsActivity.class);
                           intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                           startActivity(intent);
                           finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(LOG_TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, task.getException().toString(),
                                    Toast.LENGTH_LONG).show();

                        }
                    }
                });
    }

}
