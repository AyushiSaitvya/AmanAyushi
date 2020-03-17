package com.example.chitchat;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> list_of_groups = new ArrayList<>();
    private DatabaseReference rootref;
    private FirebaseAuth mAuth;
    private String CurrentId;

    public GroupsFragment()
    {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        group_fragment_view = inflater.inflate(R.layout.fragment_groups, container, false);

        grouplist = (ListView) group_fragment_view.findViewById(R.id.group_list);
        arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_expandable_list_item_1, list_of_groups);

        mAuth=FirebaseAuth.getInstance();
        CurrentId=mAuth.getCurrentUser().getUid();

        grouplist.setAdapter(arrayAdapter);
        rootref= FirebaseDatabase.getInstance().getReference();

        RetreiveAndDisplayGroups();

        grouplist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String group_name=parent.getItemAtPosition(position).toString();
                Intent Group_chat=new Intent (getContext(),GroupChatActivity.class);
                Group_chat.putExtra("groupname",group_name);
                startActivity(Group_chat);
            }
        });

        return group_fragment_view;

    }

    private void RetreiveAndDisplayGroups() {

        rootref.child("users").child(CurrentId).child("groups").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Set<String> set=new HashSet<>();
                Iterator iterator=dataSnapshot.getChildren().iterator();

                while(iterator.hasNext())
                {
                    set.add(((DataSnapshot)iterator.next()).getKey());
                }
                list_of_groups.clear();
                list_of_groups.addAll(set);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
