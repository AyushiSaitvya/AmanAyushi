package com.example.chitchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.text.Layout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.appcompat.widget.Toolbar;

public class chat_activity extends AppCompatActivity {


    private TextView username;
    private CircleImageView profile_img;
    private Toolbar chat_toolbar;
    private Button send_button;
    private EditText message;
    private FirebaseAuth mAuth;
    private String receiver_id,sender_id,receiver_name,receiver_image;
    private DatabaseReference root_ref;
    private final List<Messages> messages_list=new ArrayList<>();
    private LinearLayoutManager manager;
    private MessageAdapter adapter;
    private RecyclerView mrecycler_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_activity);
        chat_toolbar=(Toolbar)findViewById(R.id.chats_app_toolbar);
         mAuth=FirebaseAuth.getInstance();

         adapter=new MessageAdapter(messages_list);
         mrecycler_view=findViewById(R.id.recycler);
         manager=new LinearLayoutManager(this);
         mrecycler_view.setLayoutManager(manager);
         mrecycler_view.setAdapter(adapter);


        send_button=findViewById(R.id.send_button);
        message=findViewById(R.id.message);

        setSupportActionBar(chat_toolbar);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView=layoutInflater.inflate(R.layout.custom_bar,null);
        actionBar.setCustomView(actionBarView);
        root_ref=FirebaseDatabase.getInstance().getReference();

         username=(TextView)findViewById(R.id.profile_username);
         profile_img=(CircleImageView)findViewById(R.id.profile_pic);

         receiver_id=getIntent().getExtras().get("Visit_id").toString();
         sender_id=mAuth.getCurrentUser().getUid();
         receiver_name=getIntent().getExtras().get("Visit_name").toString();
         receiver_image=getIntent().getExtras().get("Visit_img").toString();


         username.setText(receiver_name);
         if(!receiver_image.equals(""))
                    Picasso.get().load(receiver_image).into(profile_img);

          send_button.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  sendMessage();
              }
          });



    }
    protected void onStart()
    {

        super.onStart();
        Toast.makeText(chat_activity.this,"sssfdsfs",Toast.LENGTH_SHORT).show();
        root_ref.child("Messages").child(sender_id).child(receiver_id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Messages message=dataSnapshot.getValue(Messages.class);
                messages_list.add(message);
                adapter.notifyDataSetChanged();
                mrecycler_view.smoothScrollToPosition(mrecycler_view.getAdapter().getItemCount());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void sendMessage()
    {
           String msg_txt=message.getText().toString();
           if(TextUtils.isEmpty(msg_txt))
           {
               Toast.makeText(this,"No message to Send", Toast.LENGTH_SHORT).show();
           }
           else
           {
               String messageSender_ref="Messages/"+sender_id+"/"+receiver_id;
               String messageReceiver_ref="Messages/"+receiver_id+"/"+sender_id;

               DatabaseReference Message_key_ref=root_ref.child("messages").child(sender_id).child(receiver_id).push();
               String Message_key=Message_key_ref.getKey();

               Map messageTextBody=new HashMap();
               messageTextBody.put("Message",msg_txt);
               messageTextBody.put("type","text");
               messageTextBody.put("Sender",sender_id);

               Map messagedetails=new HashMap();
               messagedetails.put(messageSender_ref+"/"+Message_key,messageTextBody);
               messagedetails.put(messageReceiver_ref+"/"+Message_key,messageTextBody);

               root_ref.updateChildren(messagedetails).addOnCompleteListener(new OnCompleteListener() {
                   @Override
                   public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful())
                        {

                        }
                        else
                        {
                            String str=task.getException().toString();
                            Toast.makeText(chat_activity.this,str,Toast.LENGTH_SHORT).show();

                        }
                        message.setText("");
                   }
               });

           }


    }
}
