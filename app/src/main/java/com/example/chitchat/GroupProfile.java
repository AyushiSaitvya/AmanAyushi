package com.example.chitchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class GroupProfile extends AppCompatActivity {
    private String GroupName;
    private FirebaseAuth mAuth;
    private String UserId;
    private String Admin;
    private DatabaseReference grp_ref,user_ref,members_ref;
    private TextView group_name
;
    private EditText change_grp_name;
    private TextView admin_Name;
    private Button update;
    private CircleImageView group_img;
    public static final int PICK_IMAGE = 1;
    private StorageReference UserProfileImagesRef;
    private String url;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private Button mButton;

    private List<GroupList> List = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_profile);

        GroupName=getIntent().getExtras().get("Group_Name").toString();

        mAuth=FirebaseAuth.getInstance();
        UserId=mAuth.getCurrentUser().getUid();
        grp_ref=FirebaseDatabase.getInstance().getReference().child("groups");
        user_ref=FirebaseDatabase.getInstance().getReference().child("users");

        group_name=findViewById(R.id.group_name);
        change_grp_name=findViewById(R.id.change_group_name);
        admin_Name=findViewById(R.id.group_admin);
        update=findViewById(R.id.Update);
        group_img=findViewById(R.id.profile_image);
        UserProfileImagesRef= FirebaseStorage.getInstance().getReference().child("ProfileImages");

        mButton=findViewById(R.id.Join);

        recyclerView = (RecyclerView) findViewById(R.id.recycler);

        mAdapter = new MyAdapter(List);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setAdapter(mAdapter);

        if(GroupName.equals("No_Grp_name"))
       {
           CreateNew();
       }
        else
        {

            retrieveUserInfo();
            manageCondition();
        }

        fetch();

    }
    private void fetch()
    {
         grp_ref.child(GroupName).child("members").addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                 if(dataSnapshot.exists())
                 {
                     final Iterator iterator=dataSnapshot.getChildren().iterator();
                     List.clear();


                     while(iterator.hasNext())
                     {
                          String str=((DataSnapshot)iterator.next()).getValue().toString();
                          GroupList grp=new GroupList(str);
                          List.add(grp);
                     }



                     mAdapter.notifyDataSetChanged();

                 }


             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });




    }




    private void manageCondition()
    {
          grp_ref.child(GroupName).addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                  if(dataSnapshot.exists())
                  {
                      Admin=dataSnapshot.child("admin").getValue().toString();

                      if(Admin.equals(UserId))
                      {
                          same();
                      }
                      else
                      {
                          different();
                      }


                  }
              }

              @Override
              public void onCancelled(@NonNull DatabaseError databaseError) {

              }
          });

    }

    private void same()
    {

        String grname=group_name.getText().toString();


        group_name.setVisibility(View.INVISIBLE);
        change_grp_name.setVisibility(View.VISIBLE);

        change_grp_name.setText(grname);



        group_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);

            }
        });
       update.setVisibility(View.VISIBLE);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str=change_grp_name.getText().toString();
                grp_ref.child(GroupName).child("name").setValue(str);

                grp_ref.child(GroupName).child("image").setValue(url);




            }

        });

    }


    private void CreateNew(){
        group_name.setVisibility(View.INVISIBLE);
         change_grp_name.setVisibility(View.VISIBLE);

//        Toast.makeText(GroupProfile.this,GroupName,Toast.LENGTH_SHORT).show();
         group_img.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent = new Intent();
                 intent.setType("image/*");
                 intent.setAction(Intent.ACTION_GET_CONTENT);
                 startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);

             }
         });
          update.setText("CREATE GROUP");
          update.setVisibility(View.VISIBLE);
           update.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   final String key = FirebaseDatabase.getInstance().getReference("groups").push().getKey();

                   String str=change_grp_name.getText().toString();
                   grp_ref.child(key).child("name").setValue(str);

                   grp_ref.child(key).child("admin").setValue(UserId);
                   grp_ref.child(key).child("image").setValue(url);


                   user_ref.child(UserId).child("name").addValueEventListener(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                           if(dataSnapshot.exists())
                           {
                               String ad_str=dataSnapshot.getValue().toString();
                               admin_Name.setText(ad_str);
                               grp_ref.child(key).child("members").child(UserId).setValue(ad_str);
                           }
                       }

                       @Override
                       public void onCancelled(@NonNull DatabaseError databaseError) {

                       }
                   });

             user_ref.child(UserId).child("groups").child(key).child("member").setValue("yes");

               }

           });

    }

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

                final StorageReference filePath = UserProfileImagesRef.child(UserId + " .jpg");
                filePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Toast.makeText(GroupProfile.this, "Upload successful!", Toast.LENGTH_LONG).show();

                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                 url = uri.toString();

                                Picasso.get().load(uri.toString()).into(group_img);
                            }
                        });

                    }
                });
            }
        }

    }

    private void  retrieveUserInfo() {
        grp_ref.child(GroupName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if((dataSnapshot.exists())&&(dataSnapshot.hasChild("name"))&&(dataSnapshot.hasChild("admin")))
                {
                    String groupname=dataSnapshot.child("name").getValue().toString();
                    String admin=dataSnapshot.child("admin").getValue().toString();

                    group_name.setVisibility(View.VISIBLE);

                    group_name.setText(groupname);

                    user_ref.child(admin).child("name").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists())
                                admin_Name.setText(dataSnapshot.getValue().toString());

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }
                if(dataSnapshot.hasChild("image"))
                {
                    String GroupImage=dataSnapshot.child("image").getValue().toString();
                    Picasso.get().load(GroupImage).into(group_img);
                }
                else
                    Toast.makeText(GroupProfile.this,"Please set your profile",Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void different()
    {

        grp_ref.child(GroupName).child("members").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!(dataSnapshot.hasChild(UserId)))
                {

                    mButton.setVisibility(View.VISIBLE);

                   mButton.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v)
                {
                    user_ref.child(UserId).child("name").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists())
                            {
                                String str=dataSnapshot.getValue().toString();
                                grp_ref.child(GroupName).child("members").child(UserId).setValue(str);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    mButton.setVisibility(View.INVISIBLE);
                    user_ref.child(UserId).child("groups").child(GroupName).child("member").setValue("yes");

                }
              });


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }


}
