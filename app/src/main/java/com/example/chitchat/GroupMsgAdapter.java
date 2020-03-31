package com.example.chitchat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
public class GroupMsgAdapter extends RecyclerView.Adapter<GroupMsgAdapter.MessageViewHolder>

{
    private List<Messages> userMessageList;
    private FirebaseAuth mAuth;
    private DatabaseReference user_ref;

    public GroupMsgAdapter(List<Messages>userMessageList)
    {
        this.userMessageList=userMessageList;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{

        public TextView senderMessageText,sender_name;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMessageText=(TextView)itemView.findViewById(R.id.sender_msg);
            sender_name=itemView.findViewById(R.id.sender_name);
        }
    }


    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.grp_msg_layout,parent,false);
        mAuth=FirebaseAuth.getInstance();
        user_ref= FirebaseDatabase.getInstance().getReference().child("users");

        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position) {

        Messages message=userMessageList.get(position);


        String type=message.getType();
        final String msg=message.getMessage();
        String sender=message.getSender();
        if(type.equals("text"))
        {

            user_ref.child(sender).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot.exists())
                    {
                        String name=dataSnapshot.child("name").getValue().toString();
                       holder.sender_name.setText(name);
                       holder.senderMessageText.setText(msg);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }
    }

    @Override
    public int getItemCount()
    {
        return userMessageList.size();
    }


}
