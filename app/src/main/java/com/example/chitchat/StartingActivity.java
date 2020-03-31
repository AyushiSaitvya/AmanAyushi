package com.example.chitchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class StartingActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mCurrentUser;
    private DatabaseReference rootref;
    private String CurrentId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting);


        mFirebaseAuth = FirebaseAuth.getInstance();
        mCurrentUser=mFirebaseAuth.getCurrentUser();
        CurrentId=mFirebaseAuth.getCurrentUser().getUid();
        rootref= FirebaseDatabase.getInstance().getReference();
    }


    protected void onStart() {
        super.onStart();

        if (mCurrentUser != null)
            verifyUser();
        else {


            Intent intent = new Intent(StartingActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();


        }

    }


    private void verifyUser()
    {
        final String currentUserUID=mCurrentUser.getUid();

        rootref.child("users").child(currentUserUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {
                    if (dataSnapshot.child("name").exists()) {
                        Intent intent = new Intent(StartingActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();


                    } else {
                        Toast.makeText(StartingActivity.this, currentUserUID, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(StartingActivity.this, SettingsActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });

    }
//


}