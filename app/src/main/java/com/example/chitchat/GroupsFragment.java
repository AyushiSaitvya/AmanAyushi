package com.example.chitchat;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
public class GroupsFragment extends Fragment {

    private ListView grouplist;
    private View group_fragment_view;

    private DatabaseReference rootref,grp_ref;
    private FirebaseAuth mAuth;
    private String CurrentId;
    private RecyclerView mRecyclerView;

    public GroupsFragment()
    {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        group_fragment_view = inflater.inflate(R.layout.fragment_groups, container, false);

        mRecyclerView = (RecyclerView) group_fragment_view.findViewById(R.id.recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth=FirebaseAuth.getInstance();
        CurrentId=mAuth.getCurrentUser().getUid();

        rootref= FirebaseDatabase.getInstance().getReference();

        grp_ref=rootref.child("users").child(CurrentId).child("groups");

        return group_fragment_view;

    }

    public void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options=new FirebaseRecyclerOptions.Builder<Contacts>().setQuery(grp_ref,Contacts.class).build();
        FirebaseRecyclerAdapter<Contacts, GroupsFragment.FindFriendViewHolder> adapter=new FirebaseRecyclerAdapter<Contacts,GroupsFragment.FindFriendViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final GroupsFragment.FindFriendViewHolder holder, final int position, @NonNull Contacts model)
            {

                final String list_id=getRef(position).getKey();
                String name;
                rootref.child("groups").child(list_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final Intent intent=new Intent(getContext(),GroupChatActivity.class);
                        if(dataSnapshot.exists())
                        {
                            String name=dataSnapshot.child("name").getValue().toString();

                            holder.userName.setText(name);
                            intent.putExtra("Grp_name",name);


                            if(dataSnapshot.hasChild("image"))
                            {
                                String image=dataSnapshot.child("image").getValue().toString();
                                Picasso.get().load(image).into(holder.profileImage);
                                intent.putExtra("Grp_img",image);
                            }
                            else
                                intent.putExtra("Grp_img","");

                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String key=getRef(position).getKey();


                                    intent.putExtra("Group_id",key);
                                    Toast.makeText(getContext(),key,Toast.LENGTH_SHORT).show();
                                    startActivity(intent);
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
            public GroupsFragment.FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_display,viewGroup,false);
                GroupsFragment.FindFriendViewHolder viewHolder=new GroupsFragment.FindFriendViewHolder(view);
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
