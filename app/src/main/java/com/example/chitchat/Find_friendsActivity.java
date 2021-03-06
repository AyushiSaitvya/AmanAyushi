package com.example.chitchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class Find_friendsActivity extends AppCompatActivity {

    private Toolbar friendsToolbar;
    private RecyclerView mrecyclerview;
    private FirebaseAuth mAuth;
    private DatabaseReference groups_ref,users_ref;
    private String Current_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        friendsToolbar=findViewById(R.id.friends_app_toolbar);
        setSupportActionBar(friendsToolbar);
        getSupportActionBar().setTitle("Find Friends");


        users_ref=FirebaseDatabase.getInstance().getReference().child("users");
        mAuth=FirebaseAuth.getInstance();
        Current_id=mAuth.getCurrentUser().getUid();

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
            protected void onBindViewHolder(@NonNull final FindFriendViewHolder holder, final int position, @NonNull Contacts model)
            {
                   if(model.getName()!=null)
                    holder.userName.setText(model.getName());
                   if(model.getStatus()!=null)

                    holder.userStatus.setText(model.getStatus());

               if(model.getImage()!=null)
                    Picasso.get().load(model.getImage()).into(holder.profileImage);


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent i=new Intent(Find_friendsActivity.this,ProfileActivity.class);
                        String Visit_user_id=getRef(position).getKey();

                        i.putExtra("Visit_user_id",Visit_user_id);

                        startActivity(i);

                    }






                });
            }

            @NonNull
            @Override
            public Find_friendsActivity.FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_display,viewGroup,false);
                Find_friendsActivity.FindFriendViewHolder viewHolder=new Find_friendsActivity.FindFriendViewHolder(view);
                return viewHolder;
            }
        };
        mrecyclerview.setAdapter(adapter);
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
