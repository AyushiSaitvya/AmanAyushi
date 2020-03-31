package com.example.chitchat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompatSideChannelService;
import androidx.recyclerview.widget.RecyclerView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>

{
    private List<Messages>userMessageList;
    private FirebaseAuth mAuth;

    public MessageAdapter(List<Messages>userMessageList)
    {
        this.userMessageList=userMessageList;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{

        public TextView senderMessageText,receiverMessageText;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMessageText=(TextView)itemView.findViewById(R.id.sender_msg);
            receiverMessageText=itemView.findViewById(R.id.receiver_msg);
        }
    }


    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                               int viewType) {
          View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_message_layout,parent,false);
          mAuth=FirebaseAuth.getInstance();


          return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position) {
            String messageSenderId=mAuth.getCurrentUser().getUid();
            Messages message=userMessageList.get(position);

            String fromId=message.getSender();
            String type=message.getType();
            String msg=message.getMessage();
           if(type.equals("text"))
           {
               holder.receiverMessageText.setVisibility(View.INVISIBLE);



               if(fromId.equals(messageSenderId))
               {
                   holder.senderMessageText.setBackgroundResource(R.drawable.sender_msg_layout);
                   holder.senderMessageText.setText(msg);
               }
               else
               {
                   holder.senderMessageText.setVisibility(View.INVISIBLE);
                   holder.receiverMessageText.setVisibility(View.VISIBLE);

                   holder.receiverMessageText.setBackgroundResource(R.drawable.receiver_msg_layout);
                   holder.receiverMessageText.setText(msg);
               }


           }
    }

    @Override
    public int getItemCount()
    {
          return userMessageList.size();
    }


}
