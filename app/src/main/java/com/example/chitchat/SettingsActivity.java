package com.example.chitchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {

    private  CircleImageView mprofileimage;
    private EditText musername;
    private EditText mthought;
    private Button mupdate;
    private DatabaseReference rootref;
    private FirebaseAuth mAuth;
    private String CurrentUserId;

    public static final int PICK_IMAGE = 1;
    private StorageReference UserProfileImagesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mprofileimage=findViewById(R.id.profile_image);
        musername=findViewById(R.id.user_name);
        mthought=findViewById(R.id.my_status);
        mupdate=findViewById(R.id.Update);
        mAuth=FirebaseAuth.getInstance();
        CurrentUserId=mAuth.getCurrentUser().getUid();
        rootref=FirebaseDatabase.getInstance().getReference();
//        getImageId();


        UserProfileImagesRef= FirebaseStorage.getInstance().getReference().child("ProfileImages");
        mupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateSettings();

            }
        });

        mprofileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });
        retreiveUserInfo();
    }


    private void retreiveUserInfo() {
        rootref.child("users").child(CurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if((dataSnapshot.exists())&&(dataSnapshot.hasChild("name"))&&(dataSnapshot.hasChild("image")))
                {
                    String username=dataSnapshot.child("name").getValue().toString();
                    String status=dataSnapshot.child("status").getValue().toString();
                    String ProfileImage=dataSnapshot.child("image").getValue().toString();
                    musername.setText(username);
                    mthought.setText(status);
                    Picasso.get().load(ProfileImage).into(mprofileimage);

                }
                else if((dataSnapshot.exists())&&(dataSnapshot.hasChild("name")))
                {
                    String username=dataSnapshot.child("name").getValue().toString();
                    String status=dataSnapshot.child("status").getValue().toString();
                    musername.setText(username);
                    mthought.setText(status);
                }
               else
                   Toast.makeText(SettingsActivity.this,"Please set your profile",Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
             if(requestCode==PICK_IMAGE&&resultCode==RESULT_OK&&data!=null)
             {
                 Uri imageUri=data.getData();
                 CropImage.activity()
                         .setGuidelines(CropImageView.Guidelines.ON)
                         .setAspectRatio(1,1)
                         .start(this);

             }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
           if(resultCode==RESULT_OK) {
               Uri resultUri = result.getUri();

               final StorageReference filePath = UserProfileImagesRef.child(CurrentUserId + " .jpg");
               filePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                   @Override
                   public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                       Toast.makeText(SettingsActivity.this, "Upload successful!", Toast.LENGTH_LONG).show();

                       filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                           @Override
                           public void onSuccess(Uri uri) {
                               String url = uri.toString();
                               rootref.child("users").child(CurrentUserId).child("image").setValue(uri.toString());
                           }
                       });

                   }
               });
           }
        }


    }
    private void UpdateSettings()
    {
        String username=musername.getText().toString();
                String thought=mthought.getText().toString();


                if(TextUtils.isEmpty(username))
                {
                    Toast.makeText(SettingsActivity.this,"UserName is Mandatory",Toast.LENGTH_SHORT).show();
                }
                if(TextUtils.isEmpty(thought))
                {
                    Toast.makeText(SettingsActivity.this,"Enter your thoughts",Toast.LENGTH_SHORT).show();
                }
                if(!(TextUtils.isEmpty(username)))
                {
                    HashMap<String, Object> profileMap = new HashMap<>();
                    profileMap.put("name", username);
                    profileMap.put("status", thought);
                    rootref.child("users").child(CurrentUserId).updateChildren(profileMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful())
                                    {

                                        Toast.makeText(SettingsActivity.this, "Profile Updated Successfully...", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        String message = task.getException().toString();
                                        Toast.makeText(SettingsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }

    }

}
