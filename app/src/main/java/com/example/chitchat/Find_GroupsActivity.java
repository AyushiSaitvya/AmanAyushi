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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import static com.example.chitchat.SettingsActivity.PICK_IMAGE;

public class Find_GroupsActivity extends AppCompatActivity {

    private Toolbar groupsToolbar;
    private RecyclerView mrecyclerview;
    private FirebaseAuth mAuth;
    private DatabaseReference groups_ref,users_ref;
    private String Current_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find__groups);
        groupsToolbar=findViewById(R.id.friends_app_toolbar);
        setSupportActionBar(groupsToolbar);
        getSupportActionBar().setTitle("Find Groups");

        groups_ref= FirebaseDatabase.getInstance().getReference().child("groups");
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

        FirebaseRecyclerOptions<Groups> options=new FirebaseRecyclerOptions.Builder<Groups>().setQuery(groups_ref,Groups.class).build();
        FirebaseRecyclerAdapter<Groups,FindFriendViewHolder> adapter=new FirebaseRecyclerAdapter<Groups,FindFriendViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FindFriendViewHolder holder, final int position, @NonNull Groups model)
            {
                if(model.getName()!=null)
                   holder.userName.setText(model.getName());



                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                           Intent intent=new Intent(Find_GroupsActivity.this,GroupProfile.class);
                           String str=getRef(position).getKey();
//                           Toast.makeText(Find_GroupsActivity.this,str,Toast.LENGTH_SHORT).show();
                           intent.putExtra("Group_Name",str);
                           startActivity(intent);
                            }






                });
            }

            @NonNull
            @Override
            public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.group_display,viewGroup,false);
                FindFriendViewHolder viewHolder=new FindFriendViewHolder(view);
                return viewHolder;
            }
        };
        mrecyclerview.setAdapter(adapter);
        adapter.startListening();
    }
    public static class FindFriendViewHolder extends RecyclerView.ViewHolder
    {
        TextView userName;


        public FindFriendViewHolder(@NonNull View itemView)
        {
            super(itemView);
            userName=itemView.findViewById(R.id.group_name);
        }
    }

}
