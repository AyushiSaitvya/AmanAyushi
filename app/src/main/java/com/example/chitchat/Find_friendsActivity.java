package com.example.chitchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class Find_friendsActivity extends AppCompatActivity {

    private Toolbar friendsToolbar;
    private RecyclerView mrecyclerview;
    private DatabaseReference users_ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);
        friendsToolbar=findViewById(R.id.friends_app_toolbar);
        setSupportActionBar(friendsToolbar);
        getSupportActionBar().setTitle("Find Friends");

        users_ref= FirebaseDatabase.getInstance().getReference().child("users");

        mrecyclerview=findViewById(R.id.recycler);
        mrecyclerview.setLayoutManager(new LinearLayoutManager(this));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }
    protected void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options=new FirebaseRecyclerOptions.Builder<Contacts>().setQuery(users_ref,Contacts.class).build();
        FirebaseRecyclerAdapter<Contacts,FindFriendViewHolder> adapter=new FirebaseRecyclerAdapter<Contacts,FindFriendViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FindFriendViewHolder holder, final int position, @NonNull Contacts model)
            {
               holder.userName.setText(model.getName());
               holder.userStatus.setText(model.getStatus());
                Picasso.get().load(model.getImage()).into(holder.profileImage);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String Visit_user_id=getRef(position).getKey();
                        Intent intent=new Intent(Find_friendsActivity.this,ProfileActivity.class);
                        intent.putExtra("Visit_user_id", Visit_user_id);
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_display,viewGroup,false);
                FindFriendViewHolder viewHolder=new FindFriendViewHolder(view);
                return viewHolder;
            }
        };
        mrecyclerview.setAdapter(adapter);
         adapter.startListening();
    }
    public static class FindFriendViewHolder extends RecyclerView.ViewHolder
    {
        TextView userName,userStatus;
        CircleImageView profileImage;


        public FindFriendViewHolder(@NonNull View itemView)
        {
            super(itemView);
            userName=itemView.findViewById(R.id.username);
            userStatus=itemView.findViewById(R.id.user_status);
            profileImage=itemView.findViewById(R.id.profile_pic);

        }
    }

}
