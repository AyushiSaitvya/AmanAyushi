package com.example.chitchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SimpleTimeZone;

import androidx.appcompat.widget.Toolbar;


public class GroupChatActivity extends AppCompatActivity {

    private String Current_group;
    private Toolbar mToolbar;
    private TextView mchat;
    private ImageButton mImage;
    private EditText mSendMsg;
    private ScrollView mscroll;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mdatabase;
    private String CurrId,curUserName;
    private DatabaseReference grpref,grp_message_key_ref;
    private DatabaseReference main_grp_ref;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        Current_group=getIntent().getExtras().get("groupname").toString();

        mchat=findViewById(R.id.group_chat_display);
        mImage=findViewById(R.id.sendto_img);
        mSendMsg=findViewById(R.id.message_text);
        mscroll=findViewById(R.id.scroll_view);
        mToolbar=findViewById(R.id.group_app_toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(Current_group);

         mAuth=FirebaseAuth.getInstance();
         mCurrentUser=mAuth.getCurrentUser();

         mdatabase=FirebaseDatabase.getInstance().getReference();
         CurrId=mCurrentUser.getUid();
         grpref=mdatabase.child("users").child(CurrId).child("groups").child(Current_group);
         main_grp_ref=mdatabase.child("groups").child(Current_group);
         getUserInfo();

         mImage.setOnClickListener(new View.OnClickListener() {
             @RequiresApi(api = Build.VERSION_CODES.N)
             @Override
             public void onClick(View v) {
                   SaveMessageForDataBase();


             }
         });
    }

    private void getUserInfo() {
        mdatabase.child("users").child(CurrId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                    curUserName=dataSnapshot.child("name").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void SaveMessageForDataBase()
    {
        String message=mSendMsg.getText().toString();
        String messageKey=grpref.push().getKey();
        if(TextUtils.isEmpty(message))
        {
             mSendMsg.setError("Type Message First");

        }
        else
        {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date();
            String Date=formatter.format(date);

            HashMap<String,Object> groupMessageKey=new HashMap<>();
            grpref.updateChildren(groupMessageKey);

            grp_message_key_ref=grpref.child(messageKey);
            HashMap<String,Object>messageInfoMap=new HashMap<>();
            messageInfoMap.put("name",curUserName);
            messageInfoMap.put("message",message);
            messageInfoMap.put("date",Date);

            grp_message_key_ref.updateChildren(messageInfoMap);

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

       main_grp_ref.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               DisplayMessages(dataSnapshot);
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });

    }




    @Override
    protected void onPause() {
        super.onPause();
        mchat.setText("");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void DisplayMessages(DataSnapshot dataSnapshot) {


        final Iterator iterator = dataSnapshot.getChildren().iterator();

        while (iterator.hasNext()) {

            String UserId = ((DataSnapshot) iterator.next()).getKey();

            mdatabase.child("users").child(UserId).child("groups").child(Current_group).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot categorySnapshot: dataSnapshot.getChildren()) {
                        String categoryName = categorySnapshot.getKey();
                           String date=categorySnapshot.child("date").getValue(String.class);
                           String message=categorySnapshot.child("message").getValue(String.class);
                           String name=categorySnapshot.child("name").getValue(String.class);

                           mchat.append(name+":\n"+message+":\n"+date+" "+"\n\n\n");
                        mscroll.fullScroll(ScrollView.FOCUS_DOWN);
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError)
                {

                }
            });
        }
    }


}
