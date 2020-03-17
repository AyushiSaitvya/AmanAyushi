package com.example.chitchat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneActivity extends AppCompatActivity {


    private EditText mobile_num;
    private Button   mButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

        mobile_num=findViewById(R.id.mob_num);
        mButton=findViewById(R.id.continue_but);


        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String  mobile=mobile_num.getText().toString().trim();
                if(mobile.isEmpty()||mobile.length()<10) {
                    mobile_num.setError("Enter a Valid Number");
                    return ;
                }
                Intent intent = new Intent(PhoneActivity.this, VerifyPhoneActivity.class);
                intent.putExtra("mobile", mobile);
                startActivity(intent);
            }
        });
    }
}
