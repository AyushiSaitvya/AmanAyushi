package com.example.chitchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SimpleTimeZone;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;


public class GroupChatActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private String Current_id;
    private String Grp_id,Grp_name,Grp_image;
    private Toolbar chat_toolbar;
    private GroupMsgAdapter adapter;
    private RecyclerView mrecycler_view;
    private RecyclerView.LayoutManager manager;
    private List<Messages> messages_list=new ArrayList<>();
    private Button send_button;
    private EditText message;
    private DatabaseReference root_ref;
    private TextView username;
    private CircleImageView profile_img;


    @Override

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        chat_toolbar=(Toolbar)findViewById(R.id.chats_app_toolbar);
        mAuth=FirebaseAuth.getInstance();
        Current_id=mAuth.getCurrentUser().getUid().toString();
        adapter=new GroupMsgAdapter(messages_list);
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
        Grp_id=getIntent().getExtras().get("Group_id").toString();
        Grp_name=getIntent().getExtras().get("Grp_name").toString();
        Grp_image=getIntent().getExtras().get("Grp_img").toString();




        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        username=(TextView)findViewById(R.id.profile_username);
        profile_img=(CircleImageView)findViewById(R.id.profile_pic);

        username.setText(Grp_name);
        if(!Grp_image.equals(""))
            Picasso.get().load(Grp_image).into(profile_img);




    }

    protected void onStart()
    {

        super.onStart();
        root_ref.child("GroupMessages").child(Grp_id).addChildEventListener(new ChildEventListener() {
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
        else {
            Toast.makeText(GroupChatActivity.this,"i am hare",Toast.LENGTH_SHORT).show();
            DatabaseReference Message_key_ref=root_ref.child("GroupMessages").child(Grp_id).push();
            String Message_key=Message_key_ref.getKey();

            Map messageTextBody=new HashMap();
            messageTextBody.put("Message",msg_txt);
            messageTextBody.put("type","text");
            messageTextBody.put("Sender",Current_id);

            root_ref.child("GroupMessages").child(Grp_id).child(Message_key).updateChildren(messageTextBody).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {

                    if(task.isSuccessful())
                    {

                    }
                    else
                    {
                        Toast.makeText(GroupChatActivity.this,task.getException().toString(),Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }
    }



}
