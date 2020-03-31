package com.example.chitchat;


import android.content.Intent;
import android.graphics.DashPathEffect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment {
    private View contact_fragment_view;
    private RecyclerView mRecyclerView;
    private DatabaseReference users_ref,members_ref;


    private FirebaseAuth mAuth;


    private String Current_id;

    public ContactsFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        contact_fragment_view = inflater.inflate(R.layout.fragment_contacts, container, false);

        mRecyclerView=(RecyclerView)contact_fragment_view.findViewById(R.id.recycler);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth= FirebaseAuth.getInstance();
        Current_id=mAuth.getCurrentUser().getUid();

        users_ref=FirebaseDatabase.getInstance().getReference().child("users");
        members_ref= FirebaseDatabase.getInstance().getReference().child("users").child(Current_id).child("requests");


        return contact_fragment_view;
    }



    public void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options=new FirebaseRecyclerOptions.Builder<Contacts>().setQuery(members_ref,Contacts.class).build();
        FirebaseRecyclerAdapter<Contacts, ContactsFragment.FindFriendViewHolder> adapter=new FirebaseRecyclerAdapter<Contacts,ContactsFragment.FindFriendViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ContactsFragment.FindFriendViewHolder holder, final int position, @NonNull Contacts model)
            {

                final String list_id=getRef(position).getKey();


                DatabaseReference ref=getRef(position).child("request_type");
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists())
                        {


                            final String str=dataSnapshot.getValue().toString();
//                            Toast.makeText(getContext(),str,Toast.LENGTH_SHORT).show();

                            users_ref.child(list_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                                         final String name = dataSnapshot.child("name").getValue().toString();
                                         String status = dataSnapshot.child("status").getValue().toString();

                                        holder.userName.setText(name);
                                        if(dataSnapshot.hasChild("image")&&str.equals("Accepted"))
                                        {
                                             String image = dataSnapshot.child("image").getValue().toString();


                                                holder.userStatus.setText(status);
                                                Picasso.get().load(image).into(holder.profileImage);
                                        }
                                        else if(str.equals("Accepted"))
                                        {
                                            holder.userStatus.setText(status);
                                        }
                                        else if(str.equals("received"))
                                        {
                                            holder.userStatus.setText("Start Chatting");

                                        }
                                        else if(str.equals("sent"))
                                        {
                                            holder.userStatus.setText("Waiting.....");
                                        }


                                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v)
                                            {
                                                String string=holder.userStatus.getText().toString();
                                                if(string.equals("Waiting.....")||string.equals("Start Chatting"))
                                                {

                                                    Intent i=new Intent(getContext(),ProfileActivity.class);
                                                    String Visit_user_id=getRef(position).getKey();
                                                    i.putExtra("Visit_user_id",Visit_user_id);
                                                    startActivity(i);
                                                }
                                                else
                                                {

                                                    Intent intent=new Intent(getContext(),chat_activity.class);
                                                    intent.putExtra("Visit_id",list_id);
                                                    intent.putExtra("Visit_name",name);
                                                    if(dataSnapshot.hasChild("image"))
                                                    {
                                                       String res=dataSnapshot.child("image").getValue().toString();
                                                        intent.putExtra("Visit_img",res);
                                                    }
                                                    else
                                                        intent.putExtra("Visit_img","");

                                                    startActivity(intent);


                                                }



                                            }
                                        });


                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });




            }

            @NonNull
            @Override
            public ContactsFragment.FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_display,viewGroup,false);
                ContactsFragment.FindFriendViewHolder viewHolder=new ContactsFragment.FindFriendViewHolder(view);
                return viewHolder;
            }
        };
        mRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }
    public static class FindFriendViewHolder extends RecyclerView.ViewHolder
    {
        TextView userName;
        TextView userStatus;
        CircleImageView profileImage;

        public FindFriendViewHolder(@NonNull View itemView)
        {
            super(itemView);
            userName=itemView.findViewById(R.id.user_name);
            userStatus=itemView.findViewById(R.id.user_status);
            profileImage=itemView.findViewById(R.id.profile_pic);


        }
    }



}
