package com.example.chitchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    private String receiverUserId,Current_state,senderUserId;
    private CircleImageView profile_img;
    private TextView user_name;
    private TextView user_status;
    private DatabaseReference Userref,chatRequest,contactsRef;
    private Button DeclineMessageRequest;
    private FirebaseAuth mAuth;
    private Button sendMessageButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth=FirebaseAuth.getInstance();
        receiverUserId=getIntent().getExtras().get("Visit_user_id").toString();
        profile_img=findViewById(R.id.src_img);
        user_name=findViewById(R.id.visit_user_name);
        user_status=findViewById(R.id.visit_user_status);
        sendMessageButton=findViewById(R.id.send_message_request);
        DeclineMessageRequest=findViewById(R.id.decline_message_request);


        chatRequest=FirebaseDatabase.getInstance().getReference().child("users");

        senderUserId=mAuth.getCurrentUser().getUid();
        Userref= FirebaseDatabase.getInstance().getReference().child("users").child(senderUserId).child("requests");

//        contactsRef=FirebaseDatabase.getInstance().getReference().child("users").child(senderUserId).child("contacts");

        Toast.makeText(this,receiverUserId,Toast.LENGTH_SHORT).show();
        Current_state="new";

        retrieveUserInfo();
        GetStatus();
        Toast.makeText(ProfileActivity.this,Current_state,Toast.LENGTH_SHORT).show();
//        ManageRequest();
    }

    private void retrieveUserInfo() {
        chatRequest.child(receiverUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                 if(dataSnapshot.exists()&&dataSnapshot.hasChild("image"))
                {
                    String name=dataSnapshot.child("name").getValue().toString();
                    user_name.setText(name);
                    String status=dataSnapshot.child("status").getValue().toString();
                    user_status.setText(status);
                    String img=dataSnapshot.child("image").getValue().toString();
                    Picasso.get().load(img).placeholder(R.drawable.person).into(profile_img);
                    

                }
                 else if(dataSnapshot.exists())
                 {
                     String name=dataSnapshot.child("name").getValue().toString();
                     user_name.setText(name);
                     String status=dataSnapshot.child("status").getValue().toString();
                     user_status.setText(status);

                 }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void GetStatus()
    {
        Userref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()&&dataSnapshot.hasChild(receiverUserId))
                {
                    String request_type=dataSnapshot.child(receiverUserId).child("request_type").getValue().toString();
                    if(request_type.equals("sent"))
                    {
                        Current_state="request_sent";
                        sendMessageButton.setText("cancel request");
                        DeclineMessageRequest.setVisibility(View.INVISIBLE);
                    }
                    if(request_type.equals("Accepted"))
                    {
                        Current_state="accepted";
                        DeclineMessageRequest.setText("Remove friend");
                        sendMessageButton.setVisibility(View.INVISIBLE);
                    }
                    if(request_type.equals("received"))
                    {
                        Current_state="received";

                        sendMessageButton.setText("Accept Request");
                        DeclineMessageRequest.setText("Decline Request");
                    }
                }

                else
                {
                    Current_state="new";

                    sendMessageButton.setText("Add Friend");
                    DeclineMessageRequest.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    private void ManageRequest()
    {
        if(!(senderUserId.equals(receiverUserId)))
        {
            if(Current_state=="request_sent")
            {
                sendMessageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CancelChatRequest();
                    }
                });
            }
            else if(Current_state=="accepted")
            {
                DeclineMessageRequest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CancelChatRequest();
                    }
                });
            }
            else if(Current_state=="received")
            {
                sendMessageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AcceptChatRequest();
                    }
                });
                DeclineMessageRequest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CancelChatRequest();
                    }
                });
            }
            else if(Current_state=="new")
            {
                sendMessageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(ProfileActivity.this,"Never Forget your Family",Toast.LENGTH_SHORT).show();
                        SendChatRequest();

                    }
                });
            }
        }








    }


    private void AcceptChatRequest()
    {
        chatRequest.child(senderUserId).child("requests").child(receiverUserId).child("request_type").setValue("Accepted").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    chatRequest.child(receiverUserId).child("requests").child(senderUserId).child("request_type").setValue("Accepted").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Current_state="accepted";
                                DeclineMessageRequest.setText("Remove friend");
                                sendMessageButton.setVisibility(View.INVISIBLE);

                            }
                        }
                    });
                }
            }
        });

    }

    private void SendChatRequest() {
         chatRequest.child(senderUserId).child("requests").child(receiverUserId).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
             @Override
             public void onComplete(@NonNull Task<Void> task) {

                 if(task.isSuccessful())
                  {
                      chatRequest.child(receiverUserId).child("requests").child(senderUserId).child("request_type").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                          @Override
                          public void onComplete(@NonNull Task<Void> task) {
                               if(task.isSuccessful())
                               {
                                   sendMessageButton.setEnabled(true);
                                   Current_state="request_sent";
                                   sendMessageButton.setText("Cancel Request");
                                   DeclineMessageRequest.setVisibility(View.INVISIBLE);
                               }

                          }
                      });
                  }

             }
         });

    }

    private void CancelChatRequest()
    {
       chatRequest.child(senderUserId).child("requests").child(receiverUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
           @Override
           public void onComplete(@NonNull Task<Void> task) {
               if(task.isSuccessful())
               {
                   chatRequest.child(receiverUserId).child("requests").child(senderUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task)
                       {
                             if(task.isSuccessful())
                             {

                                Current_state="new";
                                sendMessageButton.setText("Add Friend");
                                DeclineMessageRequest.setVisibility(View.INVISIBLE);
                             }
                       }
                   });
               }
           }
       });

    }

}
