package com.example.chitchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.audiofx.Equalizer;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private EditText mEmail, mpassword;
    private Button logbtn;
    private TextView SignUpLink;
    private TextView mForgotPassword;
    private ProgressBar mprogressBar;
    private String email;
    private String password;

    private static final String LOG_TAG = "MyActivity";
    private Button phone_but;
    private DatabaseReference rootref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        mFirebaseAuth = FirebaseAuth.getInstance();

        rootref=FirebaseDatabase.getInstance().getReference();
        mEmail = findViewById(R.id.input_email);
        mpassword = findViewById(R.id.input_password);
        logbtn = findViewById(R.id.btn_login);
        mprogressBar = findViewById(R.id.progressBar);
        SignUpLink = findViewById(R.id.link_signup);
        mForgotPassword = findViewById(R.id.Forgot_password);
        phone_but=findViewById(R.id.phone);


        logbtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                mprogressBar.setVisibility(View.VISIBLE);
//        String email, password;
                email = mEmail.getText().toString();
                password = mpassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    mprogressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), "Please enter email...", Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    mprogressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), "Please enter password!", Toast.LENGTH_LONG).show();
                    return;
                }

                mFirebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {


                        if (task.isSuccessful()) {

                                         String current_id=mFirebaseAuth.getCurrentUser().getUid();
                                         String device_token=FirebaseInstanceId.getInstance().getToken();

                                         rootref.child("users").child(current_id).child("device_token").setValue(device_token);

                                         mprogressBar.setVisibility(View.INVISIBLE);
                                         Toast.makeText(getApplicationContext(), "Logged IN SUCCESSFULLY", Toast.LENGTH_LONG).show();
                                         Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                         intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                         startActivity(intent);
                                         finish();


//



                        } else {
                            mprogressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(getApplicationContext(), "ENTER CORRECT DETAILS", Toast.LENGTH_LONG).show();


                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidUserException e) {
                                //  show error toast to user or do something
                            } catch (FirebaseAuthInvalidCredentialsException e)  {
                                //  show error toast to user or do something

                            } catch (FirebaseNetworkException e) {
                                //  show error toast to user or do something

                            } catch (Exception e) {
                                Log.e(LOG_TAG, e.getMessage());
                            }
                            Log.w(LOG_TAG, "signInWithEmail:failed",task.getException());
                        }
//                            Toast.makeText(getApplicationContext(), "Log In Failed", Toast.LENGTH_LONG);
//                            mprogressBar.setVisibility(View.GONE);
                        }


                });

            }
        });
        SignUpLink.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                                              startActivity(intent);
                                          }
                                      }
        );

        mForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplication(), "Enter your registered email id", Toast.LENGTH_SHORT).show();
                    return;
                }

                mprogressBar.setVisibility(View.VISIBLE);

                mFirebaseAuth.sendPasswordResetEmail(email)

                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                }

                                mprogressBar.setVisibility(View.GONE);
                            }
                        });
            }

        });

        phone_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,PhoneActivity.class);
                startActivity(intent);
            }
        });

    }


}