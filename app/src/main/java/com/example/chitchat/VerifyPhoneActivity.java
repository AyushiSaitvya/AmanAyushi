package com.example.chitchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class VerifyPhoneActivity extends AppCompatActivity {


    private String mVerificationId;
    private EditText mCode;
    private Button SignIn;
    private FirebaseAuth mFirebaseAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private PhoneAuthProvider.ForceResendingToken resendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);

        Intent intent = getIntent();
        String mobile = intent.getStringExtra("mobile");
        mFirebaseAuth = FirebaseAuth.getInstance();
        sendVerificationCode(mobile);
        mCode=findViewById(R.id.otp);

        findViewById(R.id.sign_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = mCode.getText().toString().trim();
                if (code.isEmpty() || code.length() < 6) {
                    mCode.setError("Enter valid code");
                    mCode.requestFocus();
                    return;
                }
            }
        });

    }

    private void sendVerificationCode(String mobile) {

       setUpVerificationCallbacks();
        PhoneAuthProvider.getInstance().verifyPhoneNumber( mobile,60,
                TimeUnit.SECONDS,
                this,
                mCallbacks);


    }
 private void setUpVerificationCallbacks() {
     mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
         @Override
         public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
             //Getting the code sent by SMS
             String code = phoneAuthCredential.getSmsCode();

             //sometime the code is not detected automatically
             //in this case the code will be null
             //so user has to manually enter the code
             if (code != null) {
                 mCode.setText(code);
                 //verifying the code
                 verifyVerificationCode(code);
             }
         }

         @Override
         public void onVerificationFailed(FirebaseException e) {
             Toast.makeText(VerifyPhoneActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
         }

         @Override
         public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {


             //storing the verification id that is sent to the user
             mVerificationId = s;
             resendToken=forceResendingToken;
         }
     };
 }

       private void  verifyVerificationCode(String code)
       {
           PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId,code);

           //signing the user
           signInWithPhoneAuthCredential(credential);
       }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(VerifyPhoneActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //verification successful we will start the profile activity
                            Intent intent = new Intent(VerifyPhoneActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        } else {

                            //verification unsuccessful.. display an error message

                            String message = "Somthing is wrong, we will fix it soon...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }

                            Snackbar snackbar = Snackbar.make(findViewById(R.id.parent), message, Snackbar.LENGTH_LONG);
                            snackbar.setAction("Dismiss", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
                            snackbar.show();
                        }
                    }
                });
    }
    }

